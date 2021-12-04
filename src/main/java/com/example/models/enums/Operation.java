package com.example.models.enums;

public enum Operation {
    CREATE {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitCreate();
        }
    },

    DROP {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitDrop();
        }
    },

    INSERT {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitInsert();
        }
    },

    SELECT {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitSelect();
        }
    },

    UPDATE {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitUpdate();
        }
    },

    DELETE {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitDelete();
        }
    },

    ALTER {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitAlter();
        }
    },

    TRUNCATE {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitTruncate();
        }
    };

    public abstract <E> E accept(OperationVisitor<E> visitor);

    public interface OperationVisitor<E> {
        E visitCreate();
        E visitDrop();
        E visitInsert();
        E visitSelect();
        E visitUpdate();
        E visitDelete();
        E visitAlter();
        E visitTruncate();
    }

}