package org.union.instalerion.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.union.common.model.ConsumingChannel;
import org.union.common.repository.ConsumingChannelRepository;
import org.union.common.service.ConsumingChannelService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.union.common.Constants.NULL_CONSUMING_CHANNEL_ERROR_MSG;

/**
 * Unit tests of {@link ConsumingChannelService}
 */
@RunWith(PowerMockRunner.class)
public class ConsumingChannelServiceTest {

    private final String ID = "ID";
    private final String ID_FROM_DB = "ID_FROM_DB";
    private final String NAME = "NAME";

    private ConsumingChannelService service;

    @Mock
    private ConsumingChannel consumingChannel;
    @Mock
    private ConsumingChannel dbConsumingChannel;
    @Mock
    private ConsumingChannelRepository repository;

    @Before
    public void setUp() {
        service = new ConsumingChannelService(repository);

        when(repository.findByName(NAME)).thenReturn(Optional.of(consumingChannel));
        when(consumingChannel.getId()).thenReturn(ID);
        when(consumingChannel.getName()).thenReturn(NAME);
        when(repository.save(consumingChannel)).thenReturn(consumingChannel);
    }

    @Test
    public void findByName() {
        ConsumingChannel consumingChannel = service.findByName(NAME);

        assertEquals(this.consumingChannel, consumingChannel);
    }

    @Test
    public void findByName_nullName_returnEmpty() {
        when(repository.findByName(null)).thenReturn(Optional.empty());

        ConsumingChannel consumingChannel = service.findByName(null);

        assertNull(consumingChannel.getId());
    }

    @Test
    public void save_nullConsumingChannel_thrownException() {
        try {
            service.save(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(NULL_CONSUMING_CHANNEL_ERROR_MSG, e.getMessage());
        }
    }

    @Test
    public void save_consumingChannelFromDb() {
        when(consumingChannel.isFromDb()).thenReturn(true);
        ConsumingChannel savedConsumingChannel = service.save(consumingChannel);

        assertEquals(consumingChannel, savedConsumingChannel);
        verify(repository, times(1)).save(consumingChannel);
    }

    @Test
    public void save_consumingChannelNotFromDb_foundInDb() {
        when(dbConsumingChannel.isFromDb()).thenReturn(true);
        when(dbConsumingChannel.getId()).thenReturn(ID_FROM_DB);
        when(repository.findByName(NAME)).thenReturn(Optional.of(dbConsumingChannel));

        when(consumingChannel.isFromDb()).thenReturn(false);
        ConsumingChannel savedConsumingChannel = service.save(consumingChannel);

        assertEquals(consumingChannel, savedConsumingChannel);
        verify(consumingChannel, times(1)).setId(ID_FROM_DB);
        verify(repository, times(1)).save(consumingChannel);
    }

    @Test
    public void save_consumingChannelNotFromDb_notFoundInDb() {
        when(repository.findByName(NAME)).thenReturn(Optional.of(dbConsumingChannel));

        when(consumingChannel.isFromDb()).thenReturn(false);
        ConsumingChannel savedConsumingChannel = service.save(consumingChannel);

        assertEquals(consumingChannel, savedConsumingChannel);
        verify(repository, times(1)).save(consumingChannel);
    }

    @Test
    public void saveAll() {
        List<ConsumingChannel> consumingChannels = Collections.singletonList(consumingChannel);

        List<ConsumingChannel> returnedConsumingChannels = service.saveAll(consumingChannels);

        assertEquals(1, returnedConsumingChannels.size());
        assertEquals(consumingChannel, returnedConsumingChannels.get(0));

        verify(repository, times(1)).save(consumingChannel);
    }

    @Test
    public void findAll() {
        when(repository.findAll()).thenReturn(Collections.singletonList(consumingChannel));
        List<ConsumingChannel> returnedConsumingChannels = service.findAll();

        assertEquals(1, returnedConsumingChannels.size());
        assertEquals(consumingChannel, returnedConsumingChannels.get(0));

        verify(repository, times(1)).findAll();
    }

    @Test
    public void remove() {
        service.remove(consumingChannel);

        verify(repository, times(1)).delete(consumingChannel);
    }

    @Test
    public void removeAll() {
        List<ConsumingChannel> consumingChannels = Collections.singletonList(consumingChannel);
        service.removeAll(consumingChannels);

        verify(repository, times(1)).deleteAll(consumingChannels);
    }
}
