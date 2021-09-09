package org.union.common.service;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import org.union.common.exception.InUseException;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.union.common.Constants.ID_CANT_BE_NULL_ERROR_MSG;

/**
 * Service to control usage of objects by ids
 */
@UtilityClass
public class UseContext {

    private final Set<String> inUseIds = new CopyOnWriteArraySet<>();

    public synchronized boolean checkInUseAndSet(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InUseException(ID_CANT_BE_NULL_ERROR_MSG);
        }

        if (inUseIds.contains(id)) {
            return true;
        }

        setInUse(id);

        return false;
    }

    public synchronized void release(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InUseException(ID_CANT_BE_NULL_ERROR_MSG);
        }

        inUseIds.remove(id);
    }

    public synchronized void setInUse(String id) {
        inUseIds.add(id);
    }
}
