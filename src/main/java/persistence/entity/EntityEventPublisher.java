package persistence.entity;

import persistence.entity.impl.event.DeleteEvent;
import persistence.entity.impl.event.LoadEvent;
import persistence.entity.impl.event.MergeEvent;
import persistence.entity.impl.event.PersistEvent;

public interface EntityEventPublisher {

    Object onLoad(LoadEvent event);

    Object onMerge(MergeEvent event);

    Object onPersist(PersistEvent event);

    void onDelete(DeleteEvent event);
}
