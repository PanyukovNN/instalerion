package org.union.common.service;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import org.union.common.exception.InUseException;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.union.common.Constants.ID_CANT_BE_NULL_ERROR_MSG;
import static org.union.common.Constants.OBJECT_IN_USE_ERROR_MSG;

/**
 * Service to control usage of objects by ids
 */
@UtilityClass
public class UseContext {

    private final Set<String> inUseIds = new CopyOnWriteArraySet<>();

    public void checkInUse(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InUseException(ID_CANT_BE_NULL_ERROR_MSG);
        }

        if (inUseIds.contains(id)) {
            throw new InUseException(String.format(OBJECT_IN_USE_ERROR_MSG, id));
        }
    }

    public void release(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InUseException(ID_CANT_BE_NULL_ERROR_MSG);
        }

        inUseIds.remove(id);
    }

    public void setInUse(String id) {
        inUseIds.add(id);
    }
}
