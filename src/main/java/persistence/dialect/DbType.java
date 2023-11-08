package persistence.dialect;

import persistence.dialect.H2.H2Dialect;
import persistence.dialect.MySQL.MySQLDialect;

public enum DbType {
	H2 {
		@Override
		public Dialect createDialect() {
			return new H2Dialect();
		}
	},
	MySQL {
		@Override
		public Dialect createDialect() {
			return new MySQLDialect();
		}
	};

	public abstract Dialect createDialect();
}

