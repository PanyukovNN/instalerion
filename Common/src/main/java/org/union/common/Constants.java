package org.union.common;

import org.union.common.service.loadingstrategy.LoadingVolume;

/**
 * Constants
 */
public class Constants {

    /**
     * Error messages
     */
    public static final String TRANSFORM_TO_VIDEO_POST_ERROR_MSG = "Произошла ошибка во время преобразования TimelineVideoMedia в VideoPost: %s";
    public static final String TRANSFORM_TO_IMAGE_POST_ERROR_MSG = "Произошла ошибка во время преобразования TimelineImageMedia в ImagePost: %s";
    public static final String PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG = "Не найден канал публикации с id %s";
    public static final String POST_NOT_FOUND_ERROR_MSG = "Не найден пост для канала публикации с id %s";
    public static final String POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG = "Не найден пост для публикации в канале %s.";
    public static final String STORY_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG = "Не найдена стори для публикации в канале %s.";
    public static final String POST_ALREADY_PUBLISHED_ERROR_MSG = "Пост уже опубликован. Повторная публикация запрещена.";
    public static final String STORY_ALREADY_PUBLISHED_ERROR_MSG = "Стори уже опубликована. Повторная публикация запрещена.";
    public static final String POST_TOO_MANY_PUBLISHING_ERRORS = "Превышено количество попыток публикации поста.";
    public static final String FILE_NOT_FOUND_ERROR_MSG = "Файл с code %s не обнаружен.";
    public static final String ERROR_WHILE_PUBLICATION = "Произошла ошибка при публикации %s";
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
    public static final String PRODUCING_CHANNEL_NULL_ID_ERROR_MSG = "Идентификатор канала публикации не может быть null.";
    public static final String LOADING_STRATEGY_TYPE_NULL_ID_ERROR_MSG = "Тип стратегии загрузки не может быть null.";
    public static final String ID_CANT_BE_NULL_ERROR_MSG = "Идентификатор не может быть равен null.";
    public static final String OBJECT_IN_USE_ERROR_MSG = "Object with id %s now in use.";
    public static final String LOADING_STRATEGY_RESOLVING_ERROR_MSG = "Ошибка при получении стратегии загрузки из контекста.";
    public static final String PUBLISHING_STRATEGY_RESOLVING_ERROR_MSG = "Ошибка при получении стратегии публикации из контекста.";
    public static final String POST_DEFINING_STRATEGY_RESOLVING_ERROR_MSG = "Ошибка при получении стратегии определения поста для публикации из контекста.";
    public static final String POST_DEFINING_STRATEGY_NOT_SET_ERROR_MSG = "Не задана стратегия определения поста для публикации.";
    public static final String POST_DEFINING_STRATEGY_NULL_ERROR_MSG = "В запросе отсутсвуте стратегия определения поста для публикации.";
    public static final String POSTS_LOADING_SUCCESS_MSG = "Загрузка постов прошла успешно.";
    public static final String NOT_FOUND_UNATTACHED_PROXY_SERVER_ERROR_MSG = "Not found unattached proxy server.";
    public static final String PROXY_SERVER_IS_NULL_ERROR_MSG = "Прокси сервер не может быть null.";
    public static final String NULL_FOR_SAVE_ERROR_MSG = "Передан null объект для сохранения";
    public static final String POST_MEDIA_INFO_IS_NULL_ERROR_MSG = "У поста нет информации о медиа.";
    public static final String CODE_COULD_NOT_BE_NULL_ERROR_MSG = "Code could not be null.";


    /**
     * Messages
     */
    public static final String PUBLISHING_REQUEST_RECEIVED_MSG = "Получен запрос на публикацию %s.";
    public static final String LOADING_REQUEST_RECEIVED_MSG = "Получен запрос на загрузку %s.";
    public static final String WORKING_ON_PAUSE_IN_NIGHT_MSG = "В период с 23:00 до 08:00 публикация не осуществляется.";
    public static final String LOADER_REQUEST_SUCCESSFULLY_SENT = "Запрос на загрузку постов успешно отправлен: %s";
    public static final String PUBLISHER_REQUEST_SUCCESSFULLY_SENT = "Запрос на публикацию поста успешно отправлен: %s";
    public static final String SAVED_IMAGES_FROM_CHANNEL_MSG = "Сохранено %d фото с канала %s";
    public static final String SAVED_VIDEOS_FROM_CHANNEL_MSG = "Сохранено %d видео с канала %s";
    public static final String PRODUCING_CHANNEL_DISABLED_MSG = "Канал публикации %s отключен.";
    public static final String PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG = "Канал публикации %s временно заблокирован инстаграммом, работа будет продолжена %s.";
    public static final String CUSTOMERS_NOT_FOUND_MSG = "Не найдено ни одного пользователя.";
    public static final String PRODUCING_CHANNELS_NOT_FOUND_MSG = "Не найдено ни одного канала публикации для пользователя %s.";
    public static final String LOADER_DISABLED_MSG = "Loader disabled.";
    public static final String PUBLISHER_DISABLED_MSG = "Publisher disabled.";
    public static final String POST_SUCCESSFULLY_PUBLISHED_MSG = "Пост %s успешно опубликован в канале %s.";
    public static final String POST_PUBLISHING_STARTED_MSG = "Начинаю публикацию поста %s в канале %s.";
    public static final String STORY_SUCCESSFULLY_PUBLISHED_MSG = "Стори %s успешно опубликована в канале %s.";
    public static final String STORY_PUBLISHING_STARTED_MSG = "Начинаю публикацию стори %s в канале %s.";
    public static final String REQUEST_FOR_PUBLICATION_COULD_BE_SENT_BEFORE_LOADING_MSG = "Запросы на публикацию не отправлены, поскольку не произведено ни одной загрузки постов.";
    public static final String PRODUCING_CHANNEL_IS_BUSY_MSG = "Канал публикации %s в настоящее время занят.";
    public static final String UNABLE_TO_DEFINE_MEDIA_TYPE_MSG = "Не удалось определить тип медиа для поста.";
    public static final String LOGIN_SESSION_DESERIALIZED_MSG = "Данные о сессии канала публикации \"%s\" восстановлены из файла.";
    public static final String LOGGED_IN_AND_SERIALIZED_MSG = "Осуществлён вход в канал публикации \"%s\", данные сохранены в файл.";
    public static final String NEW_PROXY_ATTACHED_MSG = "Last proxy of producing channel is dead. New proxy attached.";


    /**
     * Other
     */
    public static final String SOURCE_STRING_TEMPLATE = "Источник: https://www.instagram.com/p/%s/";
    public static final int PUBLISHING_ERROR_COUNT_LIMIT = 1;
    public static final double IMAGE_MATCHING_THRESHOLD = 2d;
    public static final int UNBLOCK_PRODUCING_CHANNEL_PERIOD_DAYS = 1;
    public static final LoadingVolume STANDARD_LOADING_VOLUME = new LoadingVolume(5, 15);
    // Do not change text
    public static final int IG_CLIENT_EXPIRING_HOURS = 24;
    public static final int POST_HASHTAG_NUMBER = 20;
    public static final int LAST_UNRATED_POSTS_SCAN_LIMIT = 25;
    public static final int HOURS_PASSED_FROM_TAKEN_AT_FOR_RATING_CALCULATION = 24;
    public static final int PUBLISHING_SLEEP_SECONDS = 20;
    public static final String PROXY_SERVER_ADDRESS_FORMAT = "%s:%s";
    public static final double POST_ASPECT_RATIO_THRESHOLD = 0.8;

}
