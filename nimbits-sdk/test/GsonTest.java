import com.google.gson.Gson;
import com.nimbits.client.enums.IntelligenceResultTarget;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.category.CategoryModelFactory;
import com.nimbits.client.model.category.impl.CategoryModel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.intelligence.IntelligenceModelFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModel;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 11/16/11
 * Time: 4:39 PM
 */
public class GsonTest {

    @Test
    public void testConvert() {

        Gson g = GsonFactory.getInstance();
        Point p = PointModelFactory.createPointModel(0, 0);
        List<Value> values = new ArrayList<Value>();
        for (int i = 0; i < 3; i++) {
            ValueModel v = ValueModelFactory.createValueModel(i);
            values.add(v);
            String vj = g.toJson(v);
            System.out.println(vj);
        }
        ValueModel vp = ValueModelFactory.createValueModel(1.23);
        p.setValue(vp);
        p.setValues(values);

        String json = g.toJson(p);
        Point result = g.fromJson(json, PointModel.class);
        assertNotNull(p.getValue());
        for (Value v : p.getValues()) {
            assertNotNull(v);
        }
        assertTrue(result.getValues().size() > 0);

    }

    @Test
    public void testConvertInt() {

        Gson g = GsonFactory.getInstance();
        Point p = PointModelFactory.createPointModel(0, 0);
        Intelligence i = IntelligenceModelFactory.createIntelligenceModel(true, IntelligenceResultTarget.value, 0,
                "1+1", "", true);
        p.setIntelligence(i);

        String json = g.toJson(p);
        Point result = g.fromJson(json, PointModel.class);
        assertNotNull(result.getIntelligence());
        assertEquals("1+1", result.getIntelligence().getInput());

    }

    @Test
    public void testConvertCat() {

        Gson g = GsonFactory.getInstance();
        Point p = PointModelFactory.createPointModel(0, 0);
        CategoryName categoryName = CommonFactoryLocator.getInstance().createCategoryName(UUID.randomUUID().toString());
        Category c = CategoryModelFactory.createCategoryModel(categoryName);
        c.setPoints(Arrays.asList(p));


        String json = g.toJson(c);
        Category result = g.fromJson(json, CategoryModel.class);
        assertNotNull(result.getPoints());

    }


}
