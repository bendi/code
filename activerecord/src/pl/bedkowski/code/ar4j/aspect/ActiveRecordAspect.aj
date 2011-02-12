package pl.bedkowski.code.ar4j.aspect;


import pl.bedkowski.code.ar4j.DbAccessor;
import pl.bedkowski.code.ar4j.iface.ActiveRecord;

public aspect ActiveRecordAspect {

	private DbAccessor db;

	public void setDb(DbAccessor db) {
		this.db = db;
	}

	declare parents : (@pl.bedkowski.code.ar4j.ActiveRecord *) implements ActiveRecord;

	after() returning(ActiveRecord f) : call(ActiveRecord+.new(..)) {
		db.init(f);
	}

	void around(ActiveRecord f) : call(* ActiveRecord.save(..)) && target(f) {
		db.save(f);
	}

	void around(ActiveRecord f) : call(* ActiveRecord.delete(..)) && target(f) {
		db.delete(f);
	}

	public void ActiveRecord.save() {}

	public void ActiveRecord.delete() {}

}