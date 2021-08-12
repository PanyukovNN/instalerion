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
    public static final String POST_NOT_FOUND_ERROR_MSG = "Не найден пост с id %s";
    public static final String POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG = "Пост для отправки на публикацию не найден.";
    public static final String POST_ALREADY_PUBLISHED_ERROR_MSG = "Пост уже опубликован. Повторная публикация запрещена.";
    public static final String POST_TOO_MANY_PUBLISHING_ERRORS = "Превышено количество попыток публикации поста.";
    public static final String FILE_NOT_FOUND_ERROR_MSG = "Файл с code %s не обнаружен.";
    public static final String ERROR_WHILE_PUBLICATION = "Произошла ошибка при публикации видео поста с id %s";
    public static final String ERROR_WHILE_LOADING = "Произошла ошибка при загрузке видео для клиента с id %s";
    public static final String NULL_CONSUMING_CHANNEL_ERROR_MSG = "Consuming channel is null";
    public static final String TOO_OFTEN_PUBLISHING_REQUESTS_ERROR_MSG = "Публикация отменена из-за слишком частых запросов.";
    public static final String TOO_OFTEN_LOADING_REQUESTS_ERROR_MSG = "Загрузка постов отменена из-за слишком частых запросов.";
    public static final String NO_PUBLISHING_ANSWER_FROM_INSTAGRAM_ERROR_MSG = "Нет ответа о публикации от сервера инстаграм";
    public static final String PUBLISHING_ERROR = "Ошибка публикации: %s";

    /**
     * Messages
     */
    public static final String UPLOAD_POST_REQUEST_RECEIVED_MSG = "Получен запрос на публикацию поста с id %s.";
    public static final String LOAD_POSTS_REQUEST_RECEIVED_MSG = "Получен запрос на загрузку постов аккаунта id %s.";

    /**
     * Keys
     */
    public static final String CUSTOMER_ID_KEY = "customerId";

    /**
     * Other
     */
    public static final String SOURCE_STRING_TEMPLATE = "Источник: https://www.instagram.com/p/%s/";
}
