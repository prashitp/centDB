package com.example.models.enums;

public enum Operation {
    USE {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitUse();
        }
    },
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

    COMMIT {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitCommit();
        }
    },

    START {
        public <E> E accept(OperationVisitor<E> visitor) {
            return visitor.visitStartTransaction();
        }
    };

    public abstract <E> E accept(OperationVisitor<E> visitor);

    public interface OperationVisitor<E> {
        E visitUse();
        E visitCreate();
        E visitDrop();
        E visitInsert();
        E visitSelect();
        E visitUpdate();
        E visitDelete();
        E visitCommit();
        E visitStartTransaction();
    }

}
