package utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExcelUtil {

    /**
     * Write table data (list of rows) to an xlsx file at the given path. Parent directories will be created.
     */
    public static void writeTableToExcel(List<List<String>> tableData, Path outPath) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            System.out.println("Writing Excel file with " + tableData.size() + " rows");
            // Normalize row lengths so Excel columns remain aligned
            int maxCols = 0;
            for (List<String> rowData : tableData) {
                if (rowData != null && rowData.size() > maxCols) {
                    maxCols = rowData.size();
                }
            }
            System.out.println("Max columns: " + maxCols);

            // prepare some cell styles for numeric and percent formatting
            org.apache.poi.ss.usermodel.DataFormat df = workbook.createDataFormat();
            org.apache.poi.ss.usermodel.CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(df.getFormat("0.00%"));
            org.apache.poi.ss.usermodel.CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(df.getFormat("#,##0.00"));

            int rnum = 0;
            for (List<String> rowData : tableData) {
                Row row = sheet.createRow(rnum++);
                int cnum = 0;
                if (rowData != null) {
                    for (String cellData : rowData) {
                        Cell cell = row.createCell(cnum++);
                        String s = cellData != null ? cellData.trim() : "";
                        if (s.isEmpty()) {
                            cell.setCellValue("");
                            continue;
                        }

                        // try to parse percentage values (e.g., "1.23%")
                        try {
                            if (s.endsWith("%")) {
                                double val = Double.parseDouble(s.replace("%", "").trim()) / 100.0;
                                cell.setCellValue(val);
                                cell.setCellStyle(percentStyle);
                                continue;
                            }
                        } catch (Exception ignore) {
                        }

                        // try to parse currency/number like "₹ 1,23,456" or "1,234.56"
                        String cleaned = s.replaceAll("[^0-9.-]", "");
                        if (!cleaned.isEmpty()) {
                            try {
                                double val = Double.parseDouble(cleaned);
                                cell.setCellValue(val);
                                cell.setCellStyle(numberStyle);
                                if (rnum <= 3) System.out.println("  Numeric cell: " + s + " -> " + val);
                                continue;
                            } catch (Exception ignore) {
                                // fallback to string
                            }
                        }

                        // default: write as text
                        cell.setCellValue(s);
                        if (rnum <= 3) System.out.println("  Text cell: " + s);
                    }
                }
                // Pad remaining cells with empty strings to keep consistent columns
                while (cnum < maxCols) {
                    row.createCell(cnum++).setCellValue("");
                }
            }

            // Auto-size all columns to fit content
            for (int i = 0; i < maxCols; i++) {
                sheet.autoSizeColumn(i);
            }

            Files.createDirectories(outPath.getParent());
            try (FileOutputStream fos = new FileOutputStream(outPath.toFile())) {
                workbook.write(fos);
            }
            System.out.println("Excel file written successfully to: " + outPath);
        }
    }
}

