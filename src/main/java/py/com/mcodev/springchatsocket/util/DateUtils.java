package py.com.mcodev.springchatsocket.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilidades para el formateo de fechas basadas en {@link LocalDateTime}.
 *
 * <p>Todas las implementaciones usan {@link DateTimeFormatter}, que es thread-safe,
 * a diferencia de {@link java.text.SimpleDateFormat}.</p>
 */
public final class DateUtils {

    /** Formato ISO-8601 sin zona horaria: {@code 2026-12-26T16:36:40}. */
    public static final String ISO_LOCAL_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";

    /** Formato legible por humanos: {@code 2026-12-26 16:36:40}. */
    public static final String DEFAULT_DATE_TIME_ESP = "dd/MM/yyyy HH:mm:ss";

    /** Formato solo fecha: {@code 2026-12-26}. */
    public static final String DEFAULT_DATE = "yyyy-MM-dd";

    private DateUtils() {
        // Clase de utilidades: no instanciable
    }

    /**
     * Formatea un {@link LocalDateTime} usando el patrón indicado.
     *
     * <p>Si {@code value} es {@code null}, retorna {@code null} sin lanzar excepción.
     * Si {@code formatDate} es {@code null} o está en blanco, se usa el formato
     * por defecto {@link #DEFAULT_DATE_TIME_ESP}.</p>
     *
     * @param value      fecha a formatear; puede ser {@code null}
     * @param formatDate patrón de formato (ej: {@code "yyyy-MM-dd HH:mm:ss"});
     *                   si es nulo o vacío se usa {@link #DEFAULT_DATE_TIME_ESP}
     * @return la fecha formateada como String, o {@code null} si {@code value} es {@code null}
     * @throws IllegalArgumentException si el patrón {@code formatDate} no es válido
     */
    public static String getFormatDate(LocalDateTime value, String formatDate) {
        if (value == null) {
            return null;
        }

        String pattern = (formatDate == null || formatDate.isBlank())
                ? DEFAULT_DATE_TIME_ESP
                : formatDate.trim();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return value.format(formatter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Patrón de fecha inválido: '" + pattern + "'", e);
        }
    }

    /**
     * Formatea un {@link LocalDateTime} usando el formato por defecto
     * {@link #DEFAULT_DATE_TIME_ESP} ({@code yyyy-MM-dd HH:mm:ss}).
     *
     * @param value fecha a formatear; puede ser {@code null}
     * @return la fecha formateada, o {@code null} si {@code value} es {@code null}
     */
    public static String getFormatDate(LocalDateTime value) {
        return getFormatDate(value, DEFAULT_DATE_TIME_ESP);
    }

    /**
     * Formatea un {@link Instant} usando el formato por defecto
     * {@link #DEFAULT_DATE_TIME_ESP} ({@code yyyy-MM-dd HH:mm:ss}).
     *
     * @param date fecha a formatear; puede ser {@code null}
     * @return la fecha formateada, o {@code null} si {@code date} es {@code null}
     */
    public static String getFormatDate(Instant date) {
        return date == null ? null
                : DateUtils.getFormatDate(
                LocalDateTime.ofInstant(date, ZoneId.systemDefault()),
                DateUtils.DEFAULT_DATE_TIME_ESP);
    }

    /**
     * Verifica si una cadena de texto representa una fecha válida
     * según el patrón indicado.
     *
     * @param value      cadena a validar
     * @param formatDate patrón de formato esperado
     * @return {@code true} si el texto puede parsearse con el patrón; {@code false} en caso contrario
     */
    public static boolean isValidDate(String value, String formatDate) {
        if (value == null || value.isBlank()
                || formatDate == null || formatDate.isBlank()) {
            return false;
        }
        try {
            DateTimeFormatter.ofPattern(formatDate).parse(value);
            return true;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return false;
        }
    }

}