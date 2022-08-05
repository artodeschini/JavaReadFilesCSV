package org.todeschini.binarios;

//https://github.com/yongzhidai/ToolClass

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class BigFileReader {
    private final int threadSize;
    private final String charset;
    private final int bufferSize;
    private final IHandle handle;
    private final ExecutorService  executor;
    private final long fileLength;
    private RandomAccessFile access;
    private final Set<StartEndPair> startEndPairs;
    private CyclicBarrier cyclicBarrier;
    private final AtomicLong counter = new AtomicLong(0);

    private BigFileReader(File file,IHandle handle,String charset,int bufferSize,int threadSize){
        this.fileLength = file.length();
        this.handle = handle;
        this.charset = charset;
        this.bufferSize = bufferSize;
        this.threadSize = threadSize;
        try {
            this.access = new RandomAccessFile(file,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.executor = Executors.newFixedThreadPool(threadSize);
        startEndPairs = new HashSet<BigFileReader.StartEndPair>();
    }

    public void start(){
        long everySize = this.fileLength/this.threadSize;
        try {
            calculateStartEnd(0, everySize);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final long startTime = System.currentTimeMillis();
        cyclicBarrier = new CyclicBarrier(startEndPairs.size(),new Runnable() {

            @Override
            public void run() {
                System.out.println("use time: "+(System.currentTimeMillis()-startTime));
                System.out.println("all line: "+counter.get());
            }
        });
        for(StartEndPair pair:startEndPairs){
            System.out.println("bla："+pair);
            this.executor.execute(new SliceReaderTask(pair));
        }
    }

    private void calculateStartEnd(long start,long size) throws IOException{
        if(start>fileLength-1){
            return;
        }
        StartEndPair pair = new StartEndPair();
        pair.start=start;
        long endPosition = start+size-1;
        if(endPosition>=fileLength-1){
            pair.end=fileLength-1;
            startEndPairs.add(pair);
            return;
        }

        access.seek(endPosition);
        byte tmp =(byte) access.read();
        while(tmp!='\n' && tmp!='\r'){
            endPosition++;
            if(endPosition>=fileLength-1){
                endPosition=fileLength-1;
                break;
            }
            access.seek(endPosition);
            tmp =(byte) access.read();
        }
        pair.end=endPosition;
        startEndPairs.add(pair);

        calculateStartEnd(endPosition+1, size);

    }



    public void shutdown(){
        try {
            this.access.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.executor.shutdown();
    }
    private void handle(byte[] bytes) throws UnsupportedEncodingException{
        String line = null;
        if(this.charset==null){
            line = new String(bytes);
        }else{
            line = new String(bytes,charset);
        }
        if(line!=null && !"".equals(line)){
            this.handle.handle(line);
            counter.incrementAndGet();
        }
    }
    private static class StartEndPair{
        public long start;
        public long end;

        @Override
        public String toString() {
            return "star="+start+";end="+end;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (end ^ (end >>> 32));
            result = prime * result + (int) (start ^ (start >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            StartEndPair other = (StartEndPair) obj;
            if (end != other.end)
                return false;
            return start == other.start;
        }

    }
    private class SliceReaderTask implements Runnable{
        private final long start;
        private final long sliceSize;
        private final byte[] readBuff;

        /**
         * @param start 	read position (include)
         * @param end 	the position read to(include)
         */
        public SliceReaderTask(StartEndPair pair) {
            this.start = pair.start;
            this.sliceSize = pair.end-pair.start+1;
            this.readBuff = new byte[bufferSize];
        }

        @Override
        public void run() {
            try {
                MappedByteBuffer mapBuffer = access.getChannel().map(MapMode.READ_ONLY, start, this.sliceSize);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                for(int offset=0;offset<sliceSize;offset+=bufferSize){
                    int readLength;
                    if(offset+bufferSize<=sliceSize){
                        readLength = bufferSize;
                    }else{
                        readLength = (int) (sliceSize-offset);
                    }
                    mapBuffer.get(readBuff, 0, readLength);
                    for(int i=0;i<readLength;i++){
                        byte tmp = readBuff[i];
                        if(tmp=='\n' || tmp=='\r'){
                            handle(bos.toByteArray());
                            bos.reset();
                        }else{
                            bos.write(tmp);
                        }
                    }
                }
                if(bos.size()>0){
                    handle(bos.toByteArray());
                }
                cyclicBarrier.await();//测试性能用
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static class Builder{
        private int threadSize=1;
        private String charset=null;
        private int bufferSize=1024*1024;
        private final IHandle handle;
        private final File file;
        public Builder(String file,IHandle handle){
            this.file = new File(file);
            if(!this.file.exists())
                throw new IllegalArgumentException("文件不存在！");
            this.handle = handle;
        }

        public Builder withTreahdSize(int size){
            this.threadSize = size;
            return this;
        }

        public Builder withCharset(String charset){
            this.charset= charset;
            return this;
        }

        public Builder withBufferSize(int bufferSize){
            this.bufferSize = bufferSize;
            return this;
        }

        public BigFileReader build(){
            return new BigFileReader(this.file,this.handle,this.charset,this.bufferSize,this.threadSize);
        }
    }


}
