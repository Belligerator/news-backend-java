package cz.belli.skodabackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cz.belli.skodabackend.Constants.INTERNAL_SERVER_ERROR_MESSAGE;

@Slf4j
public class Utils {

    /**
     * Converts list of entities to list of dtos.
     *
     * This is a universal generic method, so it can be used for any entity and dto.
     * It used as example of how to use generics. It would be cleaner to covert entities to dtos in the service layer.
     * Or create a converter in each entity which would convert entity to dto like this:
     * tagEntities.stream().map(TagDto::new).collect(Collectors.toList())
     *
     * @param entities  List of entities to convert.
     * @param dtoClass  Class of the dto.
     * @return          List of dtos.
     * @param <E>       Entity class.
     * @param <D>       Dto class.
     */
    public static <E, D> List<D> convertEntitiesToDTOs(List<E> entities, Class<D> dtoClass) {

        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<D> dtoList = new ArrayList<>();

        for (E entity : entities) {
            try {
                D dto = dtoClass.getConstructor(entity.getClass()).newInstance(entity);
                dtoList.add(dto);
            } catch (Exception e) {
                log.error("Error during converting entity to dto.", e);
                SentryService.captureException(e);
            }
        }
        return dtoList;
    }

    /**
     * Convert JSON string to JSON.
     */
    public static <T, D> List<T> stringToJsonObject(String stringJson, Class<D> type) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (stringJson == null) {
                return new ArrayList<>();
            } else {
                return objectMapper.readerForListOf(type).readValue(stringJson);
            }
        } catch (JsonProcessingException | UncategorizedDataAccessException e) {
            throw new ExtendedResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    INTERNAL_SERVER_ERROR_MESSAGE,
                    e.getMessage()
            );
        }
    }

    /**
     * Check if there are any errors in BindingResult and throw ExtendedResponseStatusException if there are.
     *
     * @param bindingResult BindingResult to check.
     */
    public static void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ArrayList<String> errors = new ArrayList<>();

            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                errors.add(bindingResult.getAllErrors().get(i).getDefaultMessage());
            }

            String error = String.join(" ", errors);

            throw new ExtendedResponseStatusException(HttpStatus.BAD_REQUEST, error.toString());
        }
    }

    /**
     * Generate random token.
     *
     * Source: <a href="https://www.baeldung.com/java-random-string">Baeldung</a>
     * @param length    Length of the token.
     * @return          Random token.
     */
    public static String generateRandomToken(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
