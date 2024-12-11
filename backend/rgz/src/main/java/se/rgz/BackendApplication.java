package se.rgz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import se.rgz.models.Table.Table;
import se.rgz.models.Table.TableManager;
import se.rgz.models.Table.Types.implementations.*;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:5000/")
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
/*		Table table = new Table();
		IntegerType integerType = new IntegerType();
		NumberType numberType = new NumberType();
		numberType.setValue("3.2");
		DateType dateType = new DateType();
		dateType.setValue("2020-01-01");
		GeoPositionType geoPositionType = new GeoPositionType();
		geoPositionType.setValue("5.1345245, 2.123123");
		TimeType timeType = new TimeType();
		timeType.setValue("13:15:20");
		table.addColumn(integerType);
		table.addColumn(numberType);
		table.addColumn(geoPositionType);
		table.addColumn(dateType);
		table.addColumn(timeType);
		table.addRow();
		table.cloneRow(0);
		table.setValue(new Table.Coords(1,0), "513");
		table.setValue(new Table.Coords(1,1), "1.8");
		table.sortByColumn(1);
		System.out.println(table.sum(0,1));
		System.out.println(table.getValue(new Table.Coords(0,1)).getValue());
		System.out.println(table.sum(0,1));
		System.out.println(table.getTableDescriptor().getTableJSON());*/
		//
		SpringApplication.run(BackendApplication.class, args);
    }

}
