package com.falconssoft.minipos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.falconssoft.minipos.Modle.Categories;
import com.falconssoft.minipos.Modle.Items;
import com.falconssoft.minipos.Modle.Settings;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView cats;
    private CategoryListAdapter adapter;
    private ItemListAdapter adapter2;
    private OrderedListAdapter adapter3;
    private HorizontalListView listView;
    private ListView itemsList;
    private Button saveSettings, savePay, priceOk, qtyOk;
    private ImageView save, search, clear;
    private static TextView sumNoTax, tax, sumAfterTax;
    private LinearLayout topLinear, rightLinear, back, settingsBack, reportsBack, itemsBack, saveBack, priceBack, qtyBack, functionsBack;
    private com.github.clans.fab.FloatingActionMenu menuLabelsRight;
    private com.github.clans.fab.FloatingActionButton fabAddItem, fabFunctions, fabSettings;
    ItemGridAdapter gridAdapter;
    TextView required;

    ArrayList<Items> gridItems;
    ArrayList<String> itemNo;
    String searchQuery;

    int cPrice = 0, cQty = 0;
    static double sum = 0, taxValue = 2, due;


    Dialog settingsDialog, reportsDialog, itemsDialog, saveDialog, priceDialog, functionsDialog;
    DatabaseHandler DHandler;

    ArrayList<Items> items;
    public static ArrayList<Items> items2;

    public static int theme = 9;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DHandler = new DatabaseHandler(MainActivity.this);
        required = new EditText(MainActivity.this);

        init();
        items = new ArrayList<>();
        items2 = new ArrayList<>();
        itemNo= new ArrayList<>();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridAdapter = new ItemGridAdapter(MainActivity.this, items);
                itemsDialog();

            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getResources().getString(R.string.delete_message));
                builder.setTitle(getResources().getString(R.string.delete_all));
                builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        items2.clear();
                        itemNo.clear();
                        adapter3.notifyDataSetChanged();
                        reCalculate();
                    }
                });
                builder.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items2.size() != 0)
                    saveDialog();
                else
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_items_message), Toast.LENGTH_LONG).show();

            }
        });

        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemCard.class);
                startActivity(intent);
            }
        });

        fabFunctions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functionsDialog();
            }
        });

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog();
            }
        });

        ArrayList<Categories> categories = new ArrayList<>();


        categories.add(new Categories("1", "بطاطا", R.drawable.botato));
        categories.add(new Categories("2", "برجر", R.drawable.burgerr));
        categories.add(new Categories("3", "سمك", R.drawable.fish));
        categories.add(new Categories("5", "فواكه", R.drawable.watermelon));
        categories.add(new Categories("4", "وجبات", R.drawable.corden));
        categories.add(new Categories("6", "سلطات", R.drawable.salad));
        categories.add(new Categories("7", "ليمون", R.drawable.limon));
        categories.add(new Categories("8", "فواكه", R.drawable.fruit));

        adapter = new CategoryListAdapter(MainActivity.this, categories);
        cats.setAdapter(adapter);

        cats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                items.add(new Items("1", "فراولة", 10, R.drawable.fruit, "فواكه"));
                adapter2.notifyDataSetChanged();
            }
        });


        adapter3 = new OrderedListAdapter(MainActivity.this, items2);
        itemsList.setAdapter(adapter3);

        items.add(new Items("1", "بطاطا", 10, R.drawable.botato, "خضار", 1));
        items.add(new Items("2", "برجر", 10, R.drawable.burgerr, "لحوم", 1));
        items.add(new Items("3", "سمك", 10, R.drawable.fish, "اسماك", 1));
        items.add(new Items("4", "بطيخ", 10, R.drawable.watermelon, "فواكه", 1));
        items.add(new Items("5", "كوردن بلو", 10, R.drawable.corden, "لحوم", 1));
        items.add(new Items("6", "سلطة", 10, R.drawable.salad, "خضار", 1));
        items.add(new Items("7", "ليمون", 10, R.drawable.limon, "فواكه", 1));
        items.add(new Items("8", "فراولة", 10, R.drawable.fruit, "فواكه", 1));
        items.add(new Items("9", "بطيخ", 10, R.drawable.watermelon, "فواكه", 1));
        items.add(new Items("10", "برجر", 10, R.drawable.burgerr, "لحوم", 1));
        items.add(new Items("11", "فراولة", 10, R.drawable.fruit, "فواكه", 1));
        items.add(new Items("12", "سمك", 10, R.drawable.fish, "اسماك", 1));
        items.add(new Items("13", "كوردن بلو", 10, R.drawable.corden, "لحوم", 1));
        items.add(new Items("14", "سلطة", 10, R.drawable.salad, "خضار", 1));
        items.add(new Items("15", "ليمون", 10, R.drawable.limon, "فواكه", 1));
        items.add(new Items("16", "فراولة", 10, R.drawable.fruit, "فواكه", 1));
        items.add(new Items("17", "بطيخ", 10, R.drawable.watermelon, "فواكه", 1));


        adapter2 = new ItemListAdapter(MainActivity.this, items);
        listView.setAdapter(adapter2);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (DHandler.getSettings().getControlPrice() == 0) {
                    boolean found = false;
                    int position1 = position;
                    if (items2.size() != 0) {//.indexOf
                        Log.e("fffff", "" + itemNo.indexOf(items.get(position1).getItemNo()));
                        int i = itemNo.indexOf(items.get(position1).getItemNo());
//                        for (int i = 0; i < items2.size(); i++){
                        if (i != -1) {
                            found = true;
                            double price = items2.get(i).getPrice(), qty = items2.get(i).getQty(), net = items2.get(i).getNet();
                            items2.get(i).setQty(++qty);
//                              items2.get(i).setPrice(price + 10);
                            items2.get(i).setNet(net + 10);

//                                break;
                        }
//                    }
                    }

                    if (!found) {
                        itemNo.add(items.get(position1).getItemNo());
                        items2.add(new Items(items.get(position1).getItemNo()
                                , items.get(position1).getItemName()
                                , items.get(position1).getPrice()
                                , items.get(position1).getCategory()
                                , 1
                                , (items.get(position1).getPrice() * 1)));
                    }


//                    itemsList.setAdapter(adapter3);

                    adapter3.notifyDataSetChanged();
                    reCalculate();
                }
            }
        });

        itemsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                String[] options = {
                        getResources().getString(R.string.delete_item),
                        getResources().getString(R.string.edit_price),
                        getResources().getString(R.string.edit_qty)};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Pick a color");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(getResources().getString(R.string.delete_item_message));
                            builder.setTitle(getResources().getString(R.string.delete_item));
                            builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    items2.remove(position);
                                    itemNo.remove(position);
                                    adapter3.notifyDataSetChanged();
                                    reCalculate();

                                }
                            });
                            builder.setNeutralButton(getResources().getString(R.string.clos), null);
                            builder.show();

                        } else if (which == 1) {
                            if (DHandler.getSettings().getControlPrice() == 1)
                                priceDialog(position);
                            else
                                Toast.makeText(MainActivity.this , getResources().getString(R.string.cant_edit_price_message) , Toast.LENGTH_LONG).show();

                        } else {
                            if (DHandler.getSettings().getControlQty() == 1)
                            qtyDialog(position);
                            else
                                Toast.makeText(MainActivity.this , getResources().getString(R.string.cant_edit_qty_message) , Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.show();

                return false;
            }
        });

        startAnimation();

        if (DHandler.getSettings() == null)//.getIpAddress()
            DHandler.addSettings(new Settings("", "", 9, 0, 0, "", 0, ""));
        else {
            setThemeNo(DHandler.getSettings().getThemeNo());
            theme = DHandler.getSettings().getThemeNo();
        }

    }


    void reCalculate() {

        sum = 0;
        for (int i = 0; i < items2.size(); i++) {
            sum += items2.get(i).getNet();
        }
        taxValue = 2;
        due = sum + (sum * taxValue / 100);

        sumNoTax.setText("المجموع قبل الضريبة : " + sum);
        tax.setText("الضريبة : " + taxValue);
        sumAfterTax.setText("الصافي : " + due);
//        required = new EditText(MainActivity.this);
//        required.setText("" + due);

//        saveDialog();
    }

    void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.down_from_top);
        animation.setFillAfter(true);
        rightLinear.startAnimation(animation);

        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.INFINITE, .5f, ScaleAnimation.RELATIVE_TO_SELF, .8f);
        scale.setStartOffset(500);
        scale.setDuration(700);
        scale.setInterpolator(new OvershootInterpolator());
        menuLabelsRight.startAnimation(scale);

        slideLeft(topLinear);
    }

    public void slideLeft(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                770,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(900);
        animate.setStartTime(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                550);                // toYDelta
        animate.setDuration(700);
        animate.setFillAfter(true);
        view.startAnimation(animate);

    }

    public void itemsDialog() {
        itemsDialog = new Dialog(MainActivity.this);
        itemsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        itemsDialog.setCancelable(false);
        itemsDialog.setContentView(R.layout.items_dialog);
        itemsDialog.setCanceledOnTouchOutside(true);

        final SearchView searchView = itemsDialog.findViewById(R.id.mSearchTh);
        final Spinner cat = itemsDialog.findViewById(R.id.category);
        itemsBack = itemsDialog.findViewById(R.id.items_back);
        final GridView itemsGrid = itemsDialog.findViewById(R.id.items);

        setDialogTheme(theme, itemsBack, new Button(MainActivity.this));

        searchQuery = "";
        gridItems = items;

        gridAdapter = new ItemGridAdapter(MainActivity.this, gridItems);
        itemsGrid.setAdapter(gridAdapter);

        ArrayList<String> catList = new ArrayList<>();
        catList.add("");
        catList.add("خضار");
        catList.add("فواكه");
        catList.add("لحوم");
        catList.add("اسماك");
        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catList);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat.setAdapter(catAdapter);
        cat.setSelection(0);

        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                filter(items, searchQuery, cat.getSelectedItem().toString(), itemsGrid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                searchQuery = query;
                filter(items, searchQuery, cat.getSelectedItem().toString(), itemsGrid);

                return false;
            }
        });

        itemsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                items2.add(new Items(gridItems.get(position).getItemNo(), gridItems.get(position).getItemName(),
                        gridItems.get(position).getPrice(), gridItems.get(position).getPic(), gridItems.get(position).getCategory()));
                adapter3.notifyDataSetChanged();

                Toast.makeText(MainActivity.this , getResources().getString(R.string.item_added_message), Toast.LENGTH_LONG).show();

            }
        });
        itemsDialog.show();
    }

    public void priceDialog(final int position) {
        priceDialog = new Dialog(MainActivity.this);
        priceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        priceDialog.setCancelable(false);
        priceDialog.setContentView(R.layout.price_dialog);
        priceDialog.setCanceledOnTouchOutside(true);

        priceBack = priceDialog.findViewById(R.id.price_back);
        final EditText price = priceDialog.findViewById(R.id.price);
        priceOk = priceDialog.findViewById(R.id.ok);

        setDialogTheme(theme, priceBack, priceOk);

        price.setText("" + items2.get(position).getPrice());

        priceOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!price.getText().toString().equals("")) {

                    items2.get(position).setPrice(Double.parseDouble(price.getText().toString()));
                    items2.get(position).setNet(items2.get(position).getQty() * Double.parseDouble(price.getText().toString()));
                    adapter3.notifyDataSetChanged();

                    priceDialog.dismiss();
                }
            }
        });
        priceDialog.show();

    }

    void qtyDialog(final int position){
        final Dialog qtyDialog = new Dialog(MainActivity.this);
        qtyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        qtyDialog.setContentView(R.layout.quantity_dialog);

        qtyBack = qtyDialog.findViewById(R.id.qty_back);
        final EditText qty = qtyDialog.findViewById(R.id.quantity_dialog_qty);
        qtyOk = qtyDialog.findViewById(R.id.quantity_dialog_done);

        qty.setText("" + items2.get(position).getQty());
        qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qty.setText("");
            }
        });


        setDialogTheme(theme, qtyBack, qtyOk);

        qtyOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(qty.getText().toString())) {
                    double quantity = Double.parseDouble(qty.getText().toString());

                    if (quantity > 0) {
                        items2.get(position).setQty(quantity);
                        items2.get(position).setNet(quantity * 10);
                        adapter3.notifyDataSetChanged();
                        reCalculate();
                        qtyDialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "الكمية اقل من 1!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    qty.setError("حقل فارغ!");
                }
            }
        });
        qtyDialog.show();
    }

    public void filter(ArrayList<Items> items, String item, String category, GridView itemsGrid) {

        ArrayList<Items> tempList = new ArrayList<>();
        for (int k = 0; k < items.size(); k++) {
            Log.e("******", items.get(k).getCategory() + "   " + item);
            if (
                    ((items.get(k).getItemName()).toUpperCase().contains(item) || item.equals("")) &&
                            ((items.get(k).getCategory()).equals(category) || category.equals(""))) {
                tempList.add(items.get(k));
                Log.e("******2", items.get(k).getCategory() + "   " + category + " ---- " + items.get(k).getItemName() + "   " + item);
            }
        }

        gridItems = tempList;
        Log.e("******3", "   " + gridItems.size());
        gridAdapter = new ItemGridAdapter(MainActivity.this, gridItems);
        itemsGrid.setAdapter(gridAdapter);
    }

    public void functionsDialog() {
        functionsDialog = new Dialog(MainActivity.this);
        functionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        functionsDialog.setCancelable(false);
        functionsDialog.setContentView(R.layout.functions);
        functionsDialog.setCanceledOnTouchOutside(true);

        functionsBack = functionsDialog.findViewById(R.id.functions_back);
        Button reports = functionsDialog.findViewById(R.id.reports);
        Button dayClose = functionsDialog.findViewById(R.id.day_close);

        setDialogTheme(theme, functionsBack, reports);
        setDialogTheme(theme, functionsBack, dayClose);

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDialog();
                functionsDialog.dismiss();
            }
        });

        dayClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDayDialog();
                functionsDialog.dismiss();
            }
        });
        functionsDialog.show();
    }

    public void reportDialog() {
        reportsDialog = new Dialog(MainActivity.this);
        reportsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reportsDialog.setCancelable(false);
        reportsDialog.setContentView(R.layout.reports);
        reportsDialog.setCanceledOnTouchOutside(true);

        reportsBack = reportsDialog.findViewById(R.id.reports_back);
        LinearLayout cashR = reportsDialog.findViewById(R.id.cash_report);
        LinearLayout salesR = reportsDialog.findViewById(R.id.sales_report);

        setDialogTheme(theme, reportsBack, new Button(MainActivity.this));

        cashR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CashReport.class);
                startActivity(intent);
            }
        });

        salesR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SalesReport.class);
                startActivity(intent);
            }
        });
        reportsDialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void closeDayDialog() {
        final Dialog closeDialog = new Dialog(MainActivity.this);
        closeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        closeDialog.setCancelable(false);
        closeDialog.setContentView(R.layout.close_day_dialog);
        closeDialog.setCanceledOnTouchOutside(false);

        LinearLayout closeDialogLiner = closeDialog.findViewById(R.id.closeDialogLiner);

        TextView closeDay, newDay, totalCash;
        Button close;
        close = closeDialog.findViewById(R.id.close);
        closeDay = closeDialog.findViewById(R.id.closeDay);
        newDay = closeDialog.findViewById(R.id.newDay);
        totalCash = closeDialog.findViewById(R.id.totalCash);

        setDialogTheme(theme, closeDialogLiner, close);

        closeDay.setText("21-1-2020");
        newDay.setText("21-1-2020");
        totalCash.setText("2020");


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog.dismiss();
            }
        });

        closeDialog.show();
    }

    public void settingDialog() {
        settingsDialog = new Dialog(MainActivity.this);
        settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setCancelable(false);
        settingsDialog.setContentView(R.layout.settings_dialog);
        settingsDialog.setCanceledOnTouchOutside(true);

        settingsBack = settingsDialog.findViewById(R.id.settings_back);
        final EditText ip = settingsDialog.findViewById(R.id.ip);
        final EditText company = settingsDialog.findViewById(R.id.company);
        final EditText companyID = settingsDialog.findViewById(R.id.company_id);
        final EditText posNo = settingsDialog.findViewById(R.id.pos_no);
        CheckBox price = settingsDialog.findViewById(R.id.price);
        CheckBox qty = settingsDialog.findViewById(R.id.qty);
        RadioGroup taxCalcKind = settingsDialog.findViewById(R.id.tax_type);
        RadioButton exclude = settingsDialog.findViewById(R.id.exclude);
        RadioButton include = settingsDialog.findViewById(R.id.include);

        ImageView creamDot = settingsDialog.findViewById(R.id.cream_dot);
        ImageView rosyDot = settingsDialog.findViewById(R.id.rosy_dot);
        ImageView skyDot = settingsDialog.findViewById(R.id.sky_dot);
        ImageView pronzeDot = settingsDialog.findViewById(R.id.pronze_dot);
        ImageView grayDot = settingsDialog.findViewById(R.id.gray_dot);
        ImageView redDot = settingsDialog.findViewById(R.id.red_dot);
        ImageView greenDot = settingsDialog.findViewById(R.id.green_dot);
        ImageView blueDot = settingsDialog.findViewById(R.id.blue_dot);

        saveSettings = settingsDialog.findViewById(R.id.save);

        setDialogTheme(theme, settingsBack, saveSettings);

        if (DHandler.getSettings() != null) {

            ip.setText(DHandler.getSettings().getIpAddress());
            company.setText(DHandler.getSettings().getCompanyName());

            if (DHandler.getSettings().getControlPrice() == 1) {
                price.setChecked(true);
                cPrice = 1;
            }

            if (DHandler.getSettings().getControlQty() == 1) {
                qty.setChecked(true);
                cQty = 1;
            }

            if (DHandler.getSettings().getTaxCalcKind() == 0)
                exclude.setChecked(true);
            else
                include.setChecked(true);
        }

        price.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    cPrice = 1;
                else
                    cPrice = 0;
            }
        });

        qty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    cQty = 1;
                else
                    cQty = 0;
            }
        });

        final int taxKind = taxCalcKind.getCheckedRadioButtonId() == R.id.exclude ? 0 : 1;


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;

                switch (imageView.getId()) {

                    case R.id.rosy_dot:
                        theme = 2;
                        break;
                    case R.id.green_dot:
                        theme = 3;
                        break;
                    case R.id.gray_dot:
                        theme = 4;
                        break;
                    case R.id.red_dot:
                        theme = 5;
                        break;
                    case R.id.pronze_dot:
                        theme = 6;
                        break;
                    case R.id.sky_dot:
                        theme = 7;
                        break;
                    case R.id.blue_dot:
                        theme = 8;
                        break;
                    case R.id.cream_dot:
                        theme = 9;
                        break;
                }

                setThemeNo(theme);
                setDialogTheme(theme, settingsBack, saveSettings);
                DHandler.updateTheme(theme);
            }
        };

        creamDot.setOnClickListener(onClickListener);
        rosyDot.setOnClickListener(onClickListener);
        skyDot.setOnClickListener(onClickListener);
        pronzeDot.setOnClickListener(onClickListener);
        grayDot.setOnClickListener(onClickListener);
        redDot.setOnClickListener(onClickListener);
        greenDot.setOnClickListener(onClickListener);
        blueDot.setOnClickListener(onClickListener);

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DHandler.updateSettings(new Settings(ip.getText().toString(), company.getText().toString(), theme, cPrice, cQty, companyID.getText().toString(), taxKind, posNo.getText().toString()));
                settingsDialog.dismiss();
            }
        });

        settingsDialog.show();
    }

    public void saveDialog() {
        saveDialog = new Dialog(MainActivity.this);
        saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        saveDialog.setCancelable(false);
        saveDialog.setContentView(R.layout.pay_dialog);
        saveDialog.setCanceledOnTouchOutside(true);

        saveBack = saveDialog.findViewById(R.id.save_back);
        required = saveDialog.findViewById(R.id.required);
        final EditText payed = saveDialog.findViewById(R.id.payed);
        final TextView remaining = saveDialog.findViewById(R.id.remaining);
        savePay = saveDialog.findViewById(R.id.pay);

        setDialogTheme(theme, saveBack, savePay);

        required.setText("" + due);
        payed.setText("" + due);

        payed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payed.setText("");
            }
        });

        payed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("***", payed.getText().toString());
                if (payed.getText().toString().equals(""))
                    remaining.setText("0");
                else
                    remaining.setText(convertToEnglish(String.format("%.3f", (Double.parseDouble(required.getText().toString()) - Double.parseDouble(payed.getText().toString())))));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        savePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DHandler.updateSettings(new Settings(ip.getText().toString(), company.getText().toString(), -1, cPrice, cQty));
                settingsDialog.dismiss();
            }
        });

        saveDialog.show();

    }

    void setThemeNo(int themeNo) {

        switch (themeNo) {
            case 2:
                rightLinear.setBackgroundColor(getResources().getColor(R.color.rosy1));
                topLinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.hor_shape_rosy));
                back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_rose));

                menuLabelsRight.setMenuButtonColorNormal(getResources().getColor(R.color.rosy_blue));
                menuLabelsRight.setMenuButtonColorPressed(getResources().getColor(R.color.rosy_blue));
                menuLabelsRight.setMenuButtonColorRipple(getResources().getColor(R.color.rosy_blue));

                fabAddItem.setColorNormal(getResources().getColor(R.color.rosy_blue));
                fabAddItem.setColorPressed(getResources().getColor(R.color.rosy_blue));

                fabFunctions.setColorNormal(getResources().getColor(R.color.rosy_blue));
                fabFunctions.setColorPressed(getResources().getColor(R.color.rosy_blue));

                fabSettings.setColorNormal(getResources().getColor(R.color.rosy_blue));
                fabSettings.setColorPressed(getResources().getColor(R.color.rosy_blue));

                save.setBackgroundDrawable(getResources().getDrawable(R.drawable.rosy_dot));
                search.setBackgroundDrawable(getResources().getDrawable(R.drawable.rosy_dot));
                clear.setBackgroundDrawable(getResources().getDrawable(R.drawable.rosy_dot));
                break;

            case 3:
                rightLinear.setBackgroundColor(getResources().getColor(R.color.green1));
                topLinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.hor_shape_green));
                back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_green));

                menuLabelsRight.setMenuButtonColorNormal(getResources().getColor(R.color.iguana_green));
                menuLabelsRight.setMenuButtonColorPressed(getResources().getColor(R.color.iguana_green));
                menuLabelsRight.setMenuButtonColorRipple(getResources().getColor(R.color.iguana_green));

                fabAddItem.setColorNormal(getResources().getColor(R.color.iguana_green));
                fabAddItem.setColorPressed(getResources().getColor(R.color.iguana_green));

                fabFunctions.setColorNormal(getResources().getColor(R.color.iguana_green));
                fabFunctions.setColorPressed(getResources().getColor(R.color.iguana_green));

                fabSettings.setColorNormal(getResources().getColor(R.color.iguana_green));
                fabSettings.setColorPressed(getResources().getColor(R.color.iguana_green));

                save.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_dot));
                search.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_dot));
                clear.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_dot));
                break;

            case 4:
                rightLinear.setBackgroundColor(getResources().getColor(R.color.gray1));
                topLinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.hor_shape_gray));
                back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray));

                menuLabelsRight.setMenuButtonColorNormal(getResources().getColor(R.color.gray_orange));
                menuLabelsRight.setMenuButtonColorPressed(getResources().getColor(R.color.gray_orange));
                menuLabelsRight.setMenuButtonColorRipple(getResources().getColor(R.color.gray_orange));

                fabAddItem.setColorNormal(getResources().getColor(R.color.gray_orange));
                fabAddItem.setColorPressed(getResources().getColor(R.color.gray_orange));

                fabFunctions.setColorNormal(getResources().getColor(R.color.gray_orange));
                fabFunctions.setColorPressed(getResources().getColor(R.color.gray_orange));

                fabSettings.setColorNormal(getResources().getColor(R.color.gray_orange));
                fabSettings.setColorPressed(getResources().getColor(R.color.gray_orange));

                save.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_dot));
                search.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_dot));
                clear.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_dot));
                break;

            case 5:
                rightLinear.setBackgroundColor(getResources().getColor(R.color.red1));
                topLinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.hor_shape_red));
                back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_red));

                menuLabelsRight.setMenuButtonColorNormal(getResources().getColor(R.color.red_black));
                menuLabelsRight.setMenuButtonColorPressed(getResources().getColor(R.color.red_black));
                menuLabelsRight.setMenuButtonColorRipple(getResources().getColor(R.color.red_black));

                fabAddItem.setColorNormal(getResources().getColor(R.color.red_black));
                fabAddItem.setColorPressed(getResources().getColor(R.color.red_black));

                fabFunctions.setColorNormal(getResources().getColor(R.color.red_black));
                fabFunctions.setColorPressed(getResources().getColor(R.color.red_black));

                fabSettings.setColorNormal(getResources().getColor(R.color.red_black));
                fabSettings.setColorPressed(getResources().getColor(R.color.red_black));

                save.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_dot));
                search.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_dot));
                clear.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_dot));
                break;

            case 6:
                rightLinear.setBackgroundColor(getResources().getColor(R.color.pronz1));
                topLinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.hor_shape_pronz));
                back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_pronz));

                menuLabelsRight.setMenuButtonColorNormal(getResources().getColor(R.color.red_black));
                menuLabelsRight.setMenuButtonColorPressed(getResources().getColor(R.color.red_black));
                menuLabelsRight.setMenuButtonColorRipple(getResources().getColor(R.color.red_black));

                fabAddItem.setColorNormal(getResources().getColor(R.color.red_black));
                fabAddItem.setColorPressed(getResources().getColor(R.color.red_black));

                fabFunctions.setColorNormal(getResources().getColor(R.color.red_black));
                fabFunctions.setColorPressed(getResources().getColor(R.color.red_black));

                fabSettings.setColorNormal(getResources().getColor(R.color.red_black));
                fabSettings.setColorPressed(getResources().getColor(R.color.red_black));

                save.setBackgroundDrawable(getResources().getDrawable(R.drawable.pronze_dot));
                search.setBackgroundDrawable(getResources().getDrawable(R.drawable.pronze_dot));
                clear.setBackgroundDrawable(getResources().getDrawable(R.drawable.pronze_dot));
                break;

            case 7:
                rightLinear.setBackgroundColor(getResources().getColor(R.color.sky1));
                topLinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.hor_shape_sky));
                back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_sky));

                menuLabelsRight.setMenuButtonColorNormal(getResources().getColor(R.color.sky_brown));
                menuLabelsRight.setMenuButtonColorPressed(getResources().getColor(R.color.sky_brown));
                menuLabelsRight.setMenuButtonColorRipple(getResources().getColor(R.color.sky_brown));

                fabAddItem.setColorNormal(getResources().getColor(R.color.sky_brown));
                fabAddItem.setColorPressed(getResources().getColor(R.color.sky_brown));

                fabFunctions.setColorNormal(getResources().getColor(R.color.sky_brown));
                fabFunctions.setColorPressed(getResources().getColor(R.color.sky_brown));

                fabSettings.setColorNormal(getResources().getColor(R.color.sky_brown));
                fabSettings.setColorPressed(getResources().getColor(R.color.sky_brown));

                save.setBackgroundDrawable(getResources().getDrawable(R.drawable.sky_dot));
                search.setBackgroundDrawable(getResources().getDrawable(R.drawable.sky_dot));
                clear.setBackgroundDrawable(getResources().getDrawable(R.drawable.sky_dot));
                break;

            case 8:
                rightLinear.setBackgroundColor(getResources().getColor(R.color.blue1));
                topLinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.hor_shape_blue));
                back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_blue));

                menuLabelsRight.setMenuButtonColorNormal(getResources().getColor(R.color.gray_blue));
                menuLabelsRight.setMenuButtonColorPressed(getResources().getColor(R.color.gray_blue));
                menuLabelsRight.setMenuButtonColorRipple(getResources().getColor(R.color.gray_blue));

                fabAddItem.setColorNormal(getResources().getColor(R.color.gray_blue));
                fabAddItem.setColorPressed(getResources().getColor(R.color.gray_blue));

                fabFunctions.setColorNormal(getResources().getColor(R.color.gray_blue));
                fabFunctions.setColorPressed(getResources().getColor(R.color.gray_blue));

                fabSettings.setColorNormal(getResources().getColor(R.color.gray_blue));
                fabSettings.setColorPressed(getResources().getColor(R.color.gray_blue));

                save.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_dot));
                search.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_dot));
                clear.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_dot));
                break;

            case 9:
                rightLinear.setBackgroundColor(getResources().getColor(R.color.cream_rosy));
                topLinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.hor_shape_sky));
                back.setBackgroundColor(getResources().getColor(R.color.cream));

                menuLabelsRight.setMenuButtonColorNormal(getResources().getColor(R.color.gray));
                menuLabelsRight.setMenuButtonColorPressed(getResources().getColor(R.color.gray));
                menuLabelsRight.setMenuButtonColorRipple(getResources().getColor(R.color.gray));

                fabAddItem.setColorNormal(getResources().getColor(R.color.sky_brown));
                fabAddItem.setColorPressed(getResources().getColor(R.color.sky_brown));

                fabFunctions.setColorNormal(getResources().getColor(R.color.beetle_green));
                fabFunctions.setColorPressed(getResources().getColor(R.color.beetle_green));

                fabSettings.setColorNormal(getResources().getColor(R.color.cream_rosy));
                fabSettings.setColorPressed(getResources().getColor(R.color.cream_rosy));

                save.setBackgroundDrawable(getResources().getDrawable(R.drawable.rosy_dot));
                search.setBackgroundDrawable(getResources().getDrawable(R.drawable.rosy_dot));
                clear.setBackgroundDrawable(getResources().getDrawable(R.drawable.rosy_dot));
                break;


        }
    }

    void setDialogTheme(int themeNo, LinearLayout dialogBack, Button button) {

        switch (themeNo) {
            case 2:
                dialogBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_rose));
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rosy_dot));
                break;

            case 3:
                dialogBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_green));
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_dot));
                break;

            case 4:
                dialogBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray));
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_dot));
                break;

            case 5:
                dialogBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_red));
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_dot));
                break;

            case 6:
                dialogBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_pronz));
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.pronze_dot));
                break;

            case 7:
                dialogBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_sky));
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.sky_dot));
                break;

            case 8:
                dialogBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_blue));
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_dot));
                break;

            case 9:
                dialogBack.setBackgroundColor(getResources().getColor(R.color.cream));
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rosy_dot));
                break;
        }

    }

    public String convertToEnglish(String value) {
        String newValue = (((((((((((value + "").replaceAll("١", "1")).replaceAll("٢", "2")).replaceAll("٣", "3")).replaceAll("٤", "4")).replaceAll("٥", "5")).replaceAll("٦", "6")).replaceAll("٧", "7")).replaceAll("٨", "8")).replaceAll("٩", "9")).replaceAll("٠", "0").replaceAll("٫", "."));
        return newValue;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
    }

    void init() {

        save = findViewById(R.id.save);
        search = findViewById(R.id.search);
        clear = findViewById(R.id.clear);

        cats = findViewById(R.id.categories);
        listView = findViewById(R.id.listview);
        itemsList = findViewById(R.id.list);
        topLinear = findViewById(R.id.top_linear);
        rightLinear = findViewById(R.id.right_linear);
        back = findViewById(R.id.back);

        sumNoTax = findViewById(R.id.sum_no_tax);
        tax = findViewById(R.id.tax);
        sumAfterTax = findViewById(R.id.sum_after_tax);

        menuLabelsRight = findViewById(R.id.menu_labels_right);
        fabAddItem = findViewById(R.id.fab_add_item);
        fabFunctions = findViewById(R.id.fab_function);
        fabSettings = findViewById(R.id.fab_settings);
    }
}
