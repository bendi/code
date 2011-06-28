import pl.bedkowski.code.ar4j.DbAccessor;
import pl.bedkowski.code.ar4j.aspect.ActiveRecordAspect;
import pl.bedkowski.code.ar4j.iface.ActiveRecord;
import pl.bedkowski.code.ar4j.model.Man;
import pl.bedkowski.code.ar4j.model.Woman;


public class RunMe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ActiveRecordAspect.aspectOf().setDb(new DbAccessor(){

			private void init(Woman w) {
				w.setName("Julietta");
				System.out.println("Initialized Woman with name: Julietta");
			}

			private void init(Man m) {
				m.setHeight(175);
				System.out.println("Initailized Man with height: 175");
			}

			private void save(Woman w) {
				System.out.println("Saving Woman: " + w.getName());
			}

			private void delete(Woman w) {
				System.out.println("Deleting Woman: " + w.getName());
			}

			@Override
			public void init(ActiveRecord target) {
				if (target instanceof Woman) {
					init((Woman)target);
				} else if (target instanceof Man) {
					init((Man)target);
				}
			}

			@Override
			public void save(ActiveRecord target) {
				if (target instanceof Woman) {
					save((Woman)target);
				}
			}

			@Override
			public void delete(ActiveRecord target) {
				if (target instanceof Woman) {
					delete((Woman)target);
				}
			}});
		{
			Woman w = new Woman();
			w.save();
			w.delete();
		}

		{
			Man p = new Man();
			p.save();
			System.out.println("Height of man: " + p.getHeight());
		}
	}

}
