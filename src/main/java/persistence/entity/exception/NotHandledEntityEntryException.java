package persistence.entity.exception;

import persistence.entity.persistencecontext.EntityKey;

public class NotHandledEntityEntryException extends IllegalStateException{
    public NotHandledEntityEntryException(EntityKey entityKey) {
        super(String.format("영속성 컨텍스트에서 관리되지 않는 entity 입니다. clazz: %s, id:%d", entityKey.getClassName(), entityKey.getId()));
    }
}
