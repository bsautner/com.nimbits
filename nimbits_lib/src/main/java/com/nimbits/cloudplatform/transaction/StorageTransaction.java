package com.nimbits.cloudplatform.transaction;


import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.server.gson.GsonFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * User: benjamin
 * Date: 12/28/12
 * Time: 11:59 AM
 */
public class StorageTransaction {

    public static final String TREE = "TREE";

//    protected static List<Entity> getTree(boolean refresh) throws HttpException {
//
//        List<Entity> sample = null;
//
//
//        if (refresh) {
//            deleteStoredObject(SimpleValue.getInstance(TREE));
//
//        } else {
//            sample = getStoredObject(SimpleValue.getInstance(TREE), GsonFactory.entityListType);
//        }
//
//
//        if (sample == null || sample.isEmpty()) {
//
//            final List<Entity> result = NetworkTransaction.getTree();
//            writeObjectToStorage(result, SimpleValue.getInstance(TREE));
//            return result;
//        } else {
//
//
//            return sample;
//        }
//
//
//    }

    private static boolean deleteStoredObject(final SimpleValue<String> fileName) {
        final File file = new File(Nimbits.cacheDir, fileName.toString());
        return file.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static <V> List<V> getStoredObject(final SimpleValue<String> fileName,
                                               final Type type) {

        try {

            if (Nimbits.isExternalStorageAvailable) {
                // SimpleValue<String> fileName = TransactionHelper.buildFileName(name, range, id);
                //  final File file = context.getFileStreamPath(fileName.toString());
                final File file = new File(Nimbits.cacheDir, fileName.toString());
                if (file.exists()) {

                    final StringBuilder fileContent = new StringBuilder(1024);
                    final FileInputStream fis = new FileInputStream(file);//context.openFileInput(fileName.toString());
                    final byte[] buffer = new byte[1];

                    while ((fis.read(buffer)) != -1) {
                        fileContent.append(new String(buffer));
                    }
                    fis.close();
                    final String json = fileContent.toString();

                    List<V> result;
                    try {
                        result = GsonFactory.getInstance().fromJson(json, type);
                    } catch (Exception e) {
                        file.delete();
                        return Collections.emptyList();
                    }


                    if (result != null) {

                        return result;
                    } else {

                        return Collections.emptyList();
                    }

                } else {

                    return Collections.emptyList();
                }
            } else {
                return Collections.emptyList();
            }
        } catch (FileNotFoundException e) {
            return Collections.emptyList();

        } catch (IOException e) {
            return Collections.emptyList();
        }


    }

//    private static boolean Nimbits.instance.isExternalStorageAvailable()() {
//
//        boolean mExternalStorageAvailable;
//        boolean mExternalStorageWriteable;
//        String state = Environment.getExternalStorageState();
//
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            // We can read and write the media
//            mExternalStorageAvailable = mExternalStorageWriteable = true;
//        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//            // We can only read the media
//            mExternalStorageAvailable = true;
//            mExternalStorageWriteable = false;
//        } else {
//            // Something else is wrong. It may be one of many other states, but all we need
//            //  to know is we can neither read nor write
//            mExternalStorageAvailable = mExternalStorageWriteable = false;
//        }
//        return mExternalStorageAvailable && mExternalStorageWriteable;
//    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static <T> void writeObjectToStorage(final T obj,
                                                 final SimpleValue<String> fileName) {


        if (Nimbits.isExternalStorageAvailable) {


            final FileOutputStream fos;
            try {


                File file = new File(Nimbits.cacheDir, fileName.toString());
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();


                fos = new FileOutputStream(file);// context.openFileOutput(file.getAbsolutePath() , Context.MODE_PRIVATE);
                String json = GsonFactory.getInstance().toJson(obj);
                fos.write(json.getBytes());
                fos.close();

            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }
    }

//    public static List<Value> getValue(final Entity entity) {
//        return NetworkTransaction.getValue(entity);
//    }
//
//
//    public static List<User> getSession() {
//        return NetworkTransaction.getSession();
//    }
//
//    public static List<Value> postValue(final Entity entity, final Value value) {
//        return NetworkTransaction.postValue(entity, value);
//    }
//
//    public static List<Value> getSeries(Entity entity, Range<Integer> range) {
//        return NetworkTransaction.getSeries(entity, range);
//    }
//
//    public static void deleteEntity(Entity entity) {
//        deleteStoredObject(SimpleValue.getInstance(TREE));
//        NetworkTransaction.deleteEntity(entity);
//    }
//
//    public static <T, K>  List<T> addEntity(Entity entity, Class<K> clz) {
//        deleteStoredObject(SimpleValue.getInstance(TREE));
//        return (List<T>) NetworkTransaction.addEntity(entity, clz);
//    }
//
//
//    public static <T> List<T>  updateEntity(Entity entity, Class<T> clz) {
//        return NetworkTransaction.updateEntity(entity, clz);
//    }
//
//    public static <T, K>  List<T> getEntity(final SimpleValue<String> entityId, final EntityType type, final Class<K> clz) {
//        return NetworkTransaction.getEntity(entityId, type, clz);
//    }
}
