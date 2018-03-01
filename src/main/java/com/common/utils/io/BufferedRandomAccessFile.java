package com.common.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * 添加buffer缓冲的RandomAccessFile
 * <p>
 * 只提供了读功能,减少了频繁的磁盘io操作
 * 
 * @author kevin(ssp0xd7 @ gmail.com) 01/03/2018
 */
public class BufferedRandomAccessFile extends RandomAccessFile {

    private static final int DEFAULT_SIZE = (1 << 16); // 默认 64K buffer

    private long curr_pos; // 当前位置

    private long low, high; // buffer 边界

    private byte[] buffer;

    private boolean hitEOF; // 读取到文件末尾标记

    private long disk_pos; // 当前文件中实际位置

    public BufferedRandomAccessFile(File file, String mode) throws IOException {
        this(file, mode, 0);
    }

    public BufferedRandomAccessFile(File file, String mode, int size) throws IOException {
        super(file, mode);
        this.init(size);
    }

    /**
     * init
     * 
     * @param size
     */
    private void init(int size) {
        this.low = this.curr_pos = this.high = 0;
        size = size > DEFAULT_SIZE ? size : DEFAULT_SIZE;
        this.buffer = new byte[size];
        this.hitEOF = false;
        this.disk_pos = 0L;
    }

    /**
     * buffer 填充
     *
     * @return
     * @throws IOException
     */
    private int fillBuffer() throws IOException {
        int cnt = 0;
        int rem = this.buffer.length;
        while (rem > 0) {
            int n = super.read(this.buffer, cnt, rem);
            //读到了文件末尾
            if (n < 0) {
                break;
            }
            cnt += n;
            rem -= n;
        }
        if (cnt < 0) {
            this.hitEOF = true;
            Arrays.fill(this.buffer, (byte) 0xff);
        }
        this.disk_pos += cnt;
        return cnt;
    }

    @Override
    public void close() throws IOException {
        this.buffer = null;
        super.close();
    }

    @Override
    public void seek(long pos) throws IOException {
        //判断当前pos是否在buffer内，如果不在则重新定位，填充buffer
        if (pos >= this.high || pos < this.low) {
            this.low = pos * buffer.length / buffer.length;
            if (this.disk_pos != this.low) {
                super.seek(this.low);
                this.disk_pos = this.low;
            }
            int n = this.fillBuffer();
            this.high = this.low + (long) n;
        }
        this.curr_pos = pos;
    }

    @Override
    public long getFilePointer() {
        return this.curr_pos;
    }

    @Override
    public long length() throws IOException {
        return Math.max(this.curr_pos, super.length());
    }

    @Override
    public int read() throws IOException {
        if (this.curr_pos >= this.high) {
            if (this.hitEOF) {
                return -1;
            }
            //重新填充buffer
            this.seek(this.curr_pos);
            if (this.curr_pos == this.high) {
                return -1;
            }
        }
        byte res = this.buffer[(int) (this.curr_pos - this.low)];
        this.curr_pos++;
        //byte -> int
        return ((int) res) & 0xFF;
    }
}
