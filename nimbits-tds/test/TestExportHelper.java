import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.value.ValueModelFactory;

import java.util.HashMap;
import java.util.Map;

public class TestExportHelper {

    private Map<EntityName, Point> samples() {
        final Map<EntityName, Point> points = new HashMap<EntityName, Point>();

        final EntityName name1 = CommonFactoryLocator.getInstance().createName("point1");

        Point p1 = PointModelFactory.createPointModel(1, 0);
        p1.setName(name1);

        for (int i = 0; i < 10; i++) {
            p1.getValues().add(ValueModelFactory.createValueModel(i * 3.6));

        }
        points.put(name1, p1);

        EntityName name2 = CommonFactoryLocator.getInstance().createName("point2");

        Point p2 = PointModelFactory.createPointModel(1, 0);
        p2.setName(name2);

        for (int i = 0; i < 10; i++) {
            p2.getValues().add(ValueModelFactory.createValueModel(i * 100));

        }
        points.put(name2, p2);
        return points;
    }


//    @Test
//    public void ExportPointDataToCSVSeparateColumns() {
//
//
//        String r = ExportHelperFactory.getInstance().exportPointDataToCSVSeparateColumns(samples());
//        System.out.println(r);
//
//    }
//
//     @Test
//    public void ExportPointDataToDescriptiveStatistics() throws NimbitsException {
//
//        String r = ExportHelperFactory.getInstance().exportPointDataToDescriptiveStatistics(samples());
//
//        System.out.println(r);
//
//    }

}
