package com.panyukovnn.common;

/**
 * Constants
 */
public class Constants {

    /**
     * Error messages
     */
    public static final String TRANSFORM_TO_VIDEO_POST_ERROR_MSG = "Произошла ошибка во время преобразования TimelineVideoMedia в VideoPost: %s";
    public static final String TRANSFORM_TO_IMAGE_POST_ERROR_MSG = "Произошла ошибка во время преобразования TimelineImageMedia в ImagePost: %s";
    public static final String CUSTOMER_NOT_FOUND_ERROR_MSG = "Клиент с id %s не найден";
    public static final String VIDEO_POST_NOT_FOUND_ERROR_MSG = "Видео пост с id %s не найден";
    public static final String VIDEO_POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG = "Видео пост для отправки на публикацию не найден.";
    public static final String VIDEO_POST_ALREADY_PUBLISHED_ERROR_MSG = "Пост уже опубликован. Повторная публикация запрещена.";
    public static final String VIDEO_FILE_NOT_FOUND_ERROR_MSG = "Видео файл/обложка с code %s не найдены.";
    public static final String ERROR_WHILE_PUBLICATION = "Произошла ошибка при публикации видео поста с id %s";
    public static final String ERROR_WHILE_LOADING = "Произошла ошибка при загрузке видео для клиента с id %s";

    /**
     * Messages
     */
    public static final String UPLOAD_POST_REQUEST_RECEIVED_MSG = "Получен запрос на публикацию поста с id %s.";
    public static final String LOAD_POSTS_REQUEST_RECEIVED_MSG = "Получен запрос на загрузку постов аккаунта id %s.";

    /**
     * Keys
     */
    public static final String CUSTOMER_ID_KEY = "customerId";
}
