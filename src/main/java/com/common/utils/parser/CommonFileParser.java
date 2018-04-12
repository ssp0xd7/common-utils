package com.common.utils.parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;

/**
 * @author kevin(ssp0xd7 @ gmail.com) 01/03/2018
 */
public class CommonFileParser<T> extends AbstractFileParse<T> implements FileParser<T> {

    @Override
    @SuppressWarnings("unchecked")
    public void parseCSV(final File file, final Class<T> clazz, final String charSet,
        final HashMap<String, Integer> nameIndexMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String innerCharSet = charSet;
                if (StringUtils.isBlank(charSet)) {
                    innerCharSet = getFileCharset(file);
                }
                if (nameIndexMap == null) {
                    setEnd();
                    return;
                }
                preHandle();
                setNameIndexMap(nameIndexMap);

                String line;
                try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), innerCharSet))) {
                    while ((line = br.readLine()) != null) {
                        put(Arrays.asList(line.split(",")), clazz);
                    }
                    setEnd();
                } catch (Exception e) {
                    setEnd();
                }
            }
        }).start();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void parseExcel(final File file, final Class<T> clazz, final int sheetNum,
        final HashMap<String, Integer> nameIndexMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (FileInputStream fis = new FileInputStream(file)) {
                    Workbook workbook = StreamingReader.builder().rowCacheSize(1000)// 内存中缓存行数
                        .bufferSize(8192) // 流buffer大小，和BufferedReader默认大小保持一致
                        .open(fis);
                    if (nameIndexMap == null) {
                        setEnd();
                        return;
                    }
                    preHandle();
                    setNameIndexMap(nameIndexMap);

                    List<Integer> indexs = new ArrayList<>(nameIndexMap.values());
                    Collections.sort(indexs);
                    Integer maxIndex = indexs.get(indexs.size() - 1);
                    Sheet sheet = workbook.getSheetAt(sheetNum);
                    List<String> columns = new ArrayList<>();
                    for (Row r: sheet) {
                        columns.clear();
                        for (int i = 0; i < maxIndex + 1; i++) {
                            columns.add(getCellStr(r.getCell(i)));
                        }
                        put(columns, clazz);
                    }
                    setEnd();
                } catch (Exception e) {
                    setEnd();
                }
            }
        }).start();
    }

    /**
     * 判断编码格式
     *
     * @param file
     * @return
     */
    private String getFileCharset(File file) {
        // 默认编码格式为GBK
        String charset = "GBK";
        try (FileInputStream is = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(is)) {
            byte[] first3Bytes = new byte[3];
            boolean checked = false;
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);

            if (-1 == read) {
                charset = "GBK";
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) { //FFFE
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) { //FEFF
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                && first3Bytes[2] == (byte) 0xBF) { //EFBBBF
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();

            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) {
                        // 单独出现BF以下的,也算GBK
                        break;
                    }
                    if (0x80 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            // GBK
                            continue;
                        } else {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return charset;
        }
        return charset;
    }

    /**
     * 把单元格内的类型转换至String类型
     *
     * @param cell
     */
    private String getCellStr(Cell cell) {
        //判断空单元格
        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK || StringUtils.isBlank(cell.toString())) {
            return "";
        }
        String cellStr = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:// 读取String
                cellStr = StringUtils.trim(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:// 得到Boolean对象
                cellStr = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                //日期格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    cellStr = dateFormat.format(cell.getDateCellValue());
                } else {
                    //数字
                    cellStr = StringUtils.trim(String.valueOf(cell.getNumericCellValue()));
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                // 读取公式
                cellStr = cell.getCellFormula();
                break;
            default:
                break;
        }
        return cellStr;
    }
}
