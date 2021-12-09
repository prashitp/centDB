package com.example.models.enums;

public enum Analytics {
	QUERIES {
		public <E> E accept(Analytics.AnalyticsVisitor<E> visitor) {
			return visitor.visitAllQueries();
		}
	},
	SELECT {
		public <E> E accept(Analytics.AnalyticsVisitor<E> visitor) {
			return visitor.visitByType();
		}
	},
	UPDATE {
		public <E> E accept(Analytics.AnalyticsVisitor<E> visitor) {
			return visitor.visitByType();
		}
	},
	DELETE {
		public <E> E accept(Analytics.AnalyticsVisitor<E> visitor) {
			return visitor.visitByType();
		}
	},
	INSERT {
		public <E> E accept(Analytics.AnalyticsVisitor<E> visitor) {
			return visitor.visitByType();
		}
	};
	public abstract <E> E accept(Analytics.AnalyticsVisitor<E> visitor);

	public interface AnalyticsVisitor<E> {
		E visitAllQueries();
		E visitByType();
	}
}
