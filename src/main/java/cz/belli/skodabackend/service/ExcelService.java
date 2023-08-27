package cz.belli.skodabackend.service;

import cz.belli.skodabackend.model.dto.ArticleDTO;
import cz.belli.skodabackend.model.dto.TagDTO;
import cz.belli.skodabackend.model.enumeration.LanguageEnum;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ExcelService {

    /**
     * Creates excel file from given articles.
     * @param articles  Articles to create excel file from.
     * @return          Excel file as byte array.
     */
    public static byte[] createExcel(List<ArticleDTO> articles) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // Create sheet for each language.
            for (LanguageEnum language : LanguageEnum.values()) {
                Sheet sheet = workbook.createSheet(language.name());

                Row row = sheet.createRow(0);
                row.setHeightInPoints(40);

                row.createCell(0).setCellValue("Id");
                row.createCell(1).setCellValue("Title");
                row.createCell(2).setCellValue("Body");
                row.createCell(3).setCellValue("Date of publication");
                row.createCell(4).setCellValue("Article type");
                row.createCell(5).setCellValue("Active");
                row.createCell(6).setCellValue("Tags");

                // Filter articles by language.
                List<ArticleDTO> articlesByLanguage = articles.stream()
                        .filter(article -> Objects.equals(article.getLanguage(), language.getLanguage()))
                        .collect(Collectors.toList());

                for (int i = 0; i < articlesByLanguage.size(); i++) {
                    ArticleDTO article = articlesByLanguage.get(i);

                    Row rowData = sheet.createRow(i + 1);
                    rowData.setHeightInPoints(40);

                    rowData.createCell(0).setCellValue(article.getArticleContentId());
                    rowData.createCell(1).setCellValue(article.getTitle());
                    rowData.createCell(2).setCellValue(article.getBody());
                    rowData.createCell(3).setCellValue(new SimpleDateFormat("dd.MM.yyyy").format(article.getDateOfPublication()));
                    rowData.createCell(4).setCellValue(article.getArticleType());
                    rowData.createCell(5).setCellValue(article.getActive());
                    rowData.createCell(6).setCellValue(String.join(", ", article
                            .getTags().stream()
                            .map(TagDTO::getTitle)
                            .collect(Collectors.toSet())));
                }

                // The width is specified in units of 1/256th of a character width.
                // Column 1 is Title column.
                // Column 2 is Body column.
                sheet.setColumnWidth(1, 40 * 256);
                sheet.setColumnWidth(2, 80 * 256);

                // Auto size columns Date of publication and Article type.
                sheet.autoSizeColumn(3);
                sheet.autoSizeColumn(4);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ExtendedResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while creating excel file.");
        }
    }

}
