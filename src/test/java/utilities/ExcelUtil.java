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
            int rnum = 0;
            for (List<String> rowData : tableData) {
                Row row = sheet.createRow(rnum++);
                int cnum = 0;
                for (String cellData : rowData) {
                    Cell cell = row.createCell(cnum++);
                    cell.setCellValue(cellData);
                }
            }

            Files.createDirectories(outPath.getParent());
            try (FileOutputStream fos = new FileOutputStream(outPath.toFile())) {
                workbook.write(fos);
            }
        }
    }
}

