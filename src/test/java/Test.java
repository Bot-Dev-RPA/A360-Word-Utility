import com.automationanywhere.botcommand.Actions.GetTables;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.model.table.Table;

import java.util.List;

public class Test {
	@org.testng.annotations.Test
	public void getWordTables(){
		GetTables obj = new GetTables();
		ListValue result = obj.action("E:\\Github\\wordtable.docx");
		List listOfTable = result.get();
		for(Object tablev: listOfTable){
			Table table = (Table) tablev;
			System.out.println("Headers: ");
			table.getSchema().forEach((schema -> System.out.print(schema.getName() + "|")));
			System.out.println("\nRows: "+"\n");
			table.getRows().forEach((row -> System.out.println(row.getValues())) );
		}

	}
}
