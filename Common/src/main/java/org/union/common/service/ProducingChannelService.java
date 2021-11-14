package org.union.common.service;

import org.springframework.util.CollectionUtils;
import org.union.common.Constants;
import org.union.common.exception.NotFoundException;
import org.union.common.model.ConsumingChannel;
import org.union.common.model.Customer;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.PublicationType;
import org.union.common.repository.ProducingChannelRepository;
import org.union.common.model.ChannelSubject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Producing channels service
 */
@Service
@RequiredArgsConstructor
public class ProducingChannelService {

    private final EncryptionUtil encryptionUtil;
    private final DateTimeHelper dateTimeHelper;
    private final ConsumingChannelService consumingChannelService;
    private final ProducingChannelRepository producingChannelRepository;

    public Optional<ProducingChannel> findById(String producingChannelId) {
        return producingChannelRepository.findById(producingChannelId);
    }

    public ProducingChannel save(ProducingChannel producingChannel) {
        return producingChannelRepository.save(producingChannel);
    }

    @Transactional
    public void createOrUpdate(String producingChannelId,
                               String login,
                               String password,
                               List<ConsumingChannel> consumingChannels,
                               int postPublishingPeriod,
                               int storyPublishingPeriod,
                               ChannelSubject subject,
                               List<String> hashtags,
                               Customer customer) {
        ProducingChannel producingChannel = new ProducingChannel();
        if (producingChannelId != null) {
            ProducingChannel producingChannelFromDb = producingChannelRepository.findById(producingChannelId)
                    .orElseThrow(() -> new NotFoundException(String.format(Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannelId)));
            producingChannel.setId(producingChannelFromDb.getId());
        }

        producingChannel.setLogin(login);
        producingChannel.setPassword(encryptionUtil.getTextEncryptor().encrypt(password));

        List<ConsumingChannel> savedConsumingChannels = consumingChannelService.saveAll(consumingChannels);
        producingChannel.setConsumingChannels(savedConsumingChannels);

        // All periods have to be set
        Map<PublicationType, Integer> publishingPeriodMap = producingChannel.getPublishingPeriodMap();
        publishingPeriodMap.put(PublicationType.INSTAGRAM_POST, postPublishingPeriod);
        publishingPeriodMap.put(PublicationType.INSTAGRAM_STORY, storyPublishingPeriod);

        if (subject != null && subject != ChannelSubject.NONE) {
            producingChannel.setChannelSubject(subject);
            producingChannel.setHashtags(!CollectionUtils.isEmpty(hashtags)
                    ? hashtags
                    : getDefaultHashtagsBySubject(subject));
        }

        producingChannel.setCustomer(customer);

        producingChannelRepository.save(producingChannel);
    }

    @Transactional
    public void addConsumeChannels(String producingChannelId, List<ConsumingChannel> consumingChannels) {
        ProducingChannel producingChannel = producingChannelRepository.findById(producingChannelId)
                .orElseThrow(() -> new NotFoundException(String.format(Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannelId)));

        if (producingChannel.getConsumingChannels() == null) {
            producingChannel.setConsumingChannels(new ArrayList<>());
        }

        for (ConsumingChannel consumingChannel : consumingChannels) {
            boolean matchByName = producingChannel.getConsumingChannels().stream()
                    .anyMatch(cc -> cc.getName().equals(consumingChannel.getName()));

            if (!matchByName) {
                producingChannel.getConsumingChannels().add(consumingChannel);
            }
        }

        List<ConsumingChannel> savedConsumingChannels = consumingChannelService.saveAll(producingChannel.getConsumingChannels());
        producingChannel.setConsumingChannels(savedConsumingChannels);

        producingChannelRepository.save(producingChannel);
    }

    /**
     * Is it time to publish post
     *
     * @param producingChannel producing channel
     * @return is publishing time
     */
    public boolean isPostPublishingTime(ProducingChannel producingChannel) {
        return isPublishingTime(producingChannel, PublicationType.INSTAGRAM_POST);
    }

    /**
     * Is it time to publish story
     *
     * @param producingChannel producing channel
     * @return is publishing time
     */
    public boolean isStoryPublishingTime(ProducingChannel producingChannel) {
        return isPublishingTime(producingChannel, PublicationType.INSTAGRAM_STORY);
    }

    /**
     * Is it time to load posts
     *
     * @param producingChannel producing channel
     * @return is loading time
     */
    public boolean isLoadingTime(ProducingChannel producingChannel) {
        int postingPeriod = producingChannel.getPublishingPeriodMap()
                .getOrDefault(PublicationType.INSTAGRAM_POST, 0);

        if (postingPeriod == 0) {
            return false;
        }

        int loadingPeriod = postingPeriod * 2 / 3;

        LocalDateTime lastLoadingDateTime = producingChannel.getLastLoadingDateTime();

        // if first loading
        if (lastLoadingDateTime == null) {
            return true;
        }

        int minutesFromLastLoading = dateTimeHelper.minuteFromNow(lastLoadingDateTime);

        return minutesFromLastLoading >= loadingPeriod;
    }

    public List<ProducingChannel> findAll() {
        return producingChannelRepository.findAll();
    }

    public void remove(ProducingChannel producingChannel) {
        producingChannelRepository.delete(producingChannel);
    }

    public List<ProducingChannel> findByConsumer(Customer customer) {
        return producingChannelRepository.findByCustomer(customer);
    }

    public void setBlock(ProducingChannel producingChannel) {
        producingChannel.setBlockingTime(dateTimeHelper.getCurrentDateTime());
        this.save(producingChannel);
    }

    /**
     * Channel is blocked if went less than a day from blocking
     *
     * @param producingChannel producing channel
     * @return is producing channel blocked
     */
    public boolean isBlocked(ProducingChannel producingChannel) {
        if (producingChannel.getBlockingTime() == null) {
            return false;
        }

        return producingChannel.getBlockingTime().isBefore(
                dateTimeHelper.getCurrentDateTime().minusDays(Constants.UNBLOCK_PRODUCING_CHANNEL_PERIOD_DAYS));
    }

    /**
     * Returns formatted date time when producing channel would be unblocked
     *
     * @param producingChannel producing channel
     * @return formatted date time string
     */
    public String getUnblockingFormattedDateTime(ProducingChannel producingChannel) {
        return dateTimeHelper.formatFrontDateTime(
                producingChannel.getBlockingTime().plusDays(Constants.UNBLOCK_PRODUCING_CHANNEL_PERIOD_DAYS));
    }

    private List<String> getDefaultHashtagsBySubject(ChannelSubject subject) {
        //TODO вынести в файл
        List<String> humorHashtags = Arrays.asList(
                "юмор", "жизненно", "жиза", "смешно", "смешныевидосы", "видеоинста", "смех",
                "смайлик", "камеди", "приколы", "бузова", "бородина", "дом2", "звёзды",
                "инстаприколы", "интересныефакты", "смешнаяроссия", "тутсмешно", "шутки",
                "instavideo", "шуткадня", "приколысживотными", "котята", "видеосживотными",
                "смешныеживотные", "приколдня", "прикольныеживотные", "прикольноевидео", "tiktok_russia",
                "тиктокприколы", "новостидня", "инстановости", "video_russia", "ольгабузова",
                "шоубизнес", "инсташутка", "популярноевидео", "лучшеевидео", "смешное", "животные",
                "лайк", "интересныесобытия", "позитив", "смешныесобаки", "котики",
                "милыекотики", "лучшеевидео", "домашниеживотные"
        );

        if (subject == ChannelSubject.HUMOR) {
            return humorHashtags;
        }

        return Collections.emptyList();
    }

    private boolean isPublishingTime(ProducingChannel producingChannel, PublicationType instagramPost) {
        int postingPeriod = producingChannel.getPublishingPeriodMap()
                .getOrDefault(instagramPost, 0);

        if (postingPeriod == 0) {
            return false;
        }

        LocalDateTime lastPublishingDateTime = producingChannel.getPublicationTimeMap()
                .get(instagramPost);

        // if first publication
        if (lastPublishingDateTime == null) {
            return true;
        }

        int minutesFromLastPublishing = dateTimeHelper.minuteFromNow(lastPublishingDateTime);

        return minutesFromLastPublishing >= postingPeriod;
    }
}
