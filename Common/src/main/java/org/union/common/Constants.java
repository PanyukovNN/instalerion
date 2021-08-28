package org.union.common;

/**
 * Constants
 */
public class Constants {

    /**
     * Error messages
     */
    public static final String TRANSFORM_TO_VIDEO_POST_ERROR_MSG = "Произошла ошибка во время преобразования TimelineVideoMedia в VideoPost: %s";
    public static final String TRANSFORM_TO_IMAGE_POST_ERROR_MSG = "Произошла ошибка во время преобразования TimelineImageMedia в ImagePost: %s";
    public static final String FILL_POST_INFO_ERROR_MSG = "Произошла ошибка во время преобразования поста: %s";
    public static final String PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG = "Не найден канал публикации с id %s";
    public static final String POST_NOT_FOUND_ERROR_MSG = "Не найден пост с id %s";
    public static final String POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG = "Пост для отправки на публикацию не найден.";
    public static final String POST_ALREADY_PUBLISHED_ERROR_MSG = "Пост уже опубликован. Повторная публикация запрещена.";
    public static final String POST_TOO_MANY_PUBLISHING_ERRORS = "Превышено количество попыток публикации поста.";
    public static final String FILE_NOT_FOUND_ERROR_MSG = "Файл с code %s не обнаружен.";
    public static final String ERROR_WHILE_PUBLICATION = "Произошла ошибка при публикации видео поста с id %s";
    public static final String ERROR_WHILE_LOADING = "Произошла ошибка при загрузке видео для клиента с id %s";
    public static final String NULL_CONSUMING_CHANNEL_ERROR_MSG = "Consuming channel is null";
    public static final String TOO_OFTEN_REQUESTS_ERROR_MSG = "Выполнение %s отменено из-за слишком частых запросов.";
    public static final String NO_PUBLISHING_ANSWER_FROM_INSTAGRAM_ERROR_MSG = "Нет ответа о публикации от сервера инстаграм";
    public static final String PUBLISHING_STATUS_ERROR = "Публикация неудачна, получен ответ от instagram со статусом %s, сообщение: %s";
    public static final String ERROR_WHILE_CONSUME_CHANNEL_LOADING = "Ошибка загрузки постов с канала %s";
    public static final String ERROR_WHILE_LOADER_REQUEST_SENDING = "Произошла ошибка во время отправки запроса на загрузку постов %s: %s";
    public static final String ERROR_WHILE_PUBLISHER_REQUEST_SENDING = "Произошла ошибка во время отправки запроса на публикацию поста %s: %s";
    public static final String CUSTOMER_NOT_FOUND_ERROR_MSG = "Не найден пользователь с id %s";
    public static final String UNRECOGNIZED_MEDIA_TYPE_ERROR_MSG = "Не определён медиа тип поста с id %s";
    public static final String IMAGE_POST_NOT_FOUND_ERROR_MSG = "Не найден iamge пост с id %s";
    public static final String VIDEO_POST_NOT_FOUND_ERROR_MSG = "Не найден video пост с id %s";
    public static final String TOO_LONG_VIDEO_ERROR_MSG = "Слишком длинное видео на публикацию (продолжительность ролика не должна превышать 60 секунд)";
    public static final String NO_POST_MEDIA_INFO_ERROR_MSG = "В ответе от instagram отсутствует media информация о публикации %s";
    public static final String POST_PUBLICATION_NOT_CONFIRMED_ERROR_MSG = "Публикация поста не подтверждена. Нет информации о посте %s";
    public static final String IMPOSSIBLE_TO_LOAD_IMAGE_BY_URL_ERROR_MSG = "Не удалось загрузить изображение поста для поиска дубликатов.";

    /**
     * Messages
     */
    public static final String UPLOAD_POST_REQUEST_RECEIVED_MSG = "Получен запрос на публикацию поста с id %s.";
    public static final String LOAD_POSTS_REQUEST_RECEIVED_MSG = "Получен запрос на загрузку постов аккаунта id %s.";
    public static final String WORKING_ON_PAUSE_IN_NIGHT_MSG = "В период с 23:00 до 09:00 публикация не осуществляется.";
    public static final String LOADER_REQUEST_SUCCESSFULLY_SENT = "Запрос на загрузку постов успешно отправлен: %s";
    public static final String PUBLISHER_REQUEST_SUCCESSFULLY_SENT = "Запрос на публикацию поста успешно отправлен: %s";
    public static final String SAVED_IMAGES_FROM_CHANNEL_MSG = "Сохранено %d фото с канала %s";
    public static final String SAVED_VIDEOS_FROM_CHANNEL_MSG = "Сохранено %d видео с канала %s";
    public static final String PRODUCING_CHANNEL_DISABLED_MSG = "Канал публикации %s отключен.";
    public static final String PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG = "Канал публикации %s временно заблокирован инстаграммом, работа будет продолжена %s.";
    public static final String CUSTOMERS_NOT_FOUND_MSG = "Не найдено ни одного пользователя.";
    public static final String PRODUCING_CHANNELS_NOT_FOUND_MSG = "Не найдено ни одного канала публикации для пользователя %s.";

    /**
     * Other
     */
    public static final String SOURCE_STRING_TEMPLATE = "Источник: https://www.instagram.com/p/%s/";
    public static final int PUBLISHING_ERROR_COUNT_LIMIT = 1;
    public static final double IMAGE_MATCHING_THRESHOLD = 3d;
    public static final int UNBLOCK_PRODUCING_CHANNEL_PERIOD_DAYS = 1;

}
