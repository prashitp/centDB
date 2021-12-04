package com.example.models.enums;

public enum Entity {
    DATABASE {
        public <E> E accept(EntityVisitor<E> visitor) {
            return visitor.visitDatabase();
        }
    },
    TABLE {
        public <E> E accept(EntityVisitor<E> visitor) {
            return visitor.visitTable();
        }
    };

    public abstract <E> E accept(EntityVisitor<E> visitor);

    public interface EntityVisitor<E> {
        E visitDatabase();
        E visitTable();
    }
}
