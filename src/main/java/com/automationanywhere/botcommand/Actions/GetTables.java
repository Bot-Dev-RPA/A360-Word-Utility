package com.automationanywhere.botcommand.Actions;


import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.FileExtension;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.automationanywhere.commandsdk.model.DataType.LIST;
import static com.automationanywhere.commandsdk.model.DataType.TABLE;

@BotCommand
@CommandPkg(label = "Get Tables",description = "Get list of tables present in Word file",icon = "word.svg", name = "WordTable",
		node_label = "from {{wordFilePath}} and assign to {{returnTo}}",return_description = "Returns list of table",
		return_required = true,
		return_type = LIST, return_sub_type =TABLE)
public class GetTables {
@Execute
	public ListValue action(
			@Idx(index = "1", type =AttributeType.FILE)
			@Pkg(label = "Select file to get tables from")
			@NotEmpty
			@FileExtension("docx") String wordFilePath

	) {
		try {
			if(wordFilePath==null || wordFilePath.isBlank())
				throw new BotCommandException("Entered file path is invalid");

			if (!wordFilePath.toUpperCase().endsWith(".DOCX"))
				throw new BotCommandException("Please select a supported file to continue");

			if(!new File(wordFilePath).isFile())
				throw new BotCommandException("Entered file path "+wordFilePath+" is not a valid file");

			ListValue retListValue = new ListValue();
			ArrayList<TableValue> returnList = new ArrayList<>();

			File file = new File(wordFilePath);
			try(FileInputStream fis = new FileInputStream(file)){

				XWPFDocument doc = new XWPFDocument(fis);
				List<XWPFTable> tables = doc.getTables();

				for(XWPFTable  table : tables) {

					List<Schema> schemaList = new ArrayList<>();
					List<Row> rowList = new ArrayList<>();
					int maxColumnCount;
					List<XWPFTableRow> rows = table.getRows();
					if(rows.size()>0){
						if(rows.get(0).getTableCells().size()>0)
							rows.get(0).getTableCells().forEach(header -> schemaList.add(new Schema(header.getText())));

						maxColumnCount = schemaList.size();
						for(int row=1;row<rows.size();row++) {
							List<Value> rowValue = new ArrayList<>();
							rows.get(row).getTableCells().forEach(data -> rowValue.add(new StringValue(data.getText())));

							if(rowValue.size()>maxColumnCount)
								maxColumnCount = rowValue.size();

							rowList.add(new Row(rowValue));
						}

						while(schemaList.size()< maxColumnCount)
							schemaList.add(new Schema(""));
					}

					Table Output = new Table(schemaList,rowList);
					returnList.add(new TableValue(Output));
				}

				retListValue.set(returnList);
				return retListValue;
			}


		} catch (Exception e) {
			throw new BotCommandException("Error Occurred while finding table in word document: " + e);
		}

	}
}
