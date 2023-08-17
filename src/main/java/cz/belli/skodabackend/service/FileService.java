package cz.belli.skodabackend.service;

import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;

import static cz.belli.skodabackend.Constants.INTERNAL_SERVER_ERROR_MESSAGE;

@Slf4j
@Service
public class FileService {

    public static final String UPLOADS_FOLDER = "uploads/images";
    public static final String[] ALLOWED_CONTENT_TYPES = {"image/png", "image/jpeg", "image/gif"};

    /**
     * Store uploaded file to disk.
     * @param uploadedFile  Uploaded file.
     * @return              Path to stored file.
     */
    public String storeFile(MultipartFile uploadedFile) {
        try {
            if (uploadedFile == null || uploadedFile.isEmpty()) {
                return null;
            }

            if (!this.checkFileContentType(uploadedFile)) {
                throw new ExtendedResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPG, JPEG, PNG and GIF files are allowed.");
            }

            String fileName = this.generateFileName(uploadedFile);

            File file = new File(UPLOADS_FOLDER + '/' + fileName);

            if (Files.notExists(file.getParentFile().toPath())) {
                Files.createDirectories(file.getParentFile().toPath());
            }

            uploadedFile.transferTo(file.toPath());

            return fileName;
        } catch (IOException e) {
            // todo log error
            e.printStackTrace();
            throw new ExtendedResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    INTERNAL_SERVER_ERROR_MESSAGE,
                    "Error while storing file.");

        }
    }

    /**
     * Delete file from disk. Used when article creation fails.
     * @param fileName  Name of the file to be deleted.
     */
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        log.info("Deleting file: " + fileName);
        File file = new File(UPLOADS_FOLDER + '/' + fileName);
        if (file.exists()) {
            file.delete();
        }
        log.info("File deleted: " + fileName);
    }

    /**
     * Check if uploaded file has allowed content type.
     * @param uploadedFile  Uploaded file.
     * @return              True if file has allowed content type, false otherwise.
     */
    private boolean checkFileContentType(MultipartFile uploadedFile) {
        return Arrays.asList(ALLOWED_CONTENT_TYPES).contains(uploadedFile.getContentType());
    }

    /**
     * Generate random file name with original extension.
     * @param uploadedFile  Uploaded file.
     * @return              Generated file name with original extension.
     */
    private String generateFileName(MultipartFile uploadedFile) {
        if (uploadedFile == null || uploadedFile.isEmpty()) {
            return null;
        }

        String fileName = UUID.randomUUID().toString();
        String extension = uploadedFile.getOriginalFilename().split("\\.")[1];
        return fileName + '.' + extension;
    }
}
