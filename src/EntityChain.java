public class EntityChain {
    private Entity entity;
    private EntityChain next;

    public EntityChain(Entity entity) {
        this.entity = entity;
        next = null;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityChain getNext() {
        return next;
    }

    public void setNext(Entity entity) {
        next = new EntityChain(entity);
    }
}