package com.bin.david.smarttable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.column.ColumnInfo;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.draw.MultiLineDrawFormat;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.data.format.IFormat;
import com.bin.david.form.data.format.count.ICountFormat;
import com.bin.david.form.data.format.draw.BitmapDrawFormat;
import com.bin.david.form.data.format.draw.ImageResDrawFormat;
import com.bin.david.form.data.format.draw.TextImageDrawFormat;
import com.bin.david.form.data.format.tip.MultiLineBubbleTip;
import com.bin.david.form.data.format.title.TitleImageDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.listener.OnColumnClickListener;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bin.david.form.utils.DensityUtils;
import com.bin.david.smarttable.bean.ChildData;
import com.bin.david.smarttable.bean.TableStyle;
import com.bin.david.smarttable.bean.TanBean;
import com.bin.david.smarttable.bean.UserInfo;
import com.bin.david.smarttable.view.BaseCheckDialog;
import com.bin.david.smarttable.view.BaseDialog;
import com.bin.david.smarttable.view.QuickChartDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.daivd.chart.component.axis.BaseAxis;
import com.daivd.chart.component.base.IAxis;
import com.daivd.chart.component.base.IComponent;
import com.daivd.chart.core.LineChart;
import com.daivd.chart.data.ChartData;
import com.daivd.chart.data.LineData;
import com.daivd.chart.data.style.PointStyle;
import com.daivd.chart.provider.component.cross.VerticalCross;
import com.daivd.chart.provider.component.level.LevelLine;
import com.daivd.chart.provider.component.mark.BubbleMarkView;
import com.daivd.chart.provider.component.point.Point;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MultParseModeActivity extends AppCompatActivity implements View.OnClickListener{

    private SmartTable<UserInfo> table;
    private BaseCheckDialog<TableStyle> chartDialog;
    private QuickChartDialog quickChartDialog;
    private Map<String,Bitmap> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        quickChartDialog = new QuickChartDialog();
        FontStyle.setDefaultTextSize(DensityUtils.sp2px(this,15)); //????????????????????????
        table = (SmartTable<UserInfo>) findViewById(R.id.table);
        final List<UserInfo> testData = new ArrayList<>();
        Random random = new Random();
        List<TanBean> tanBeans = TanBean.initDatas();
        //?????? ?????????????????????url
        int urlSize = tanBeans.size();
        for(int i = 0;i <50; i++) {
            UserInfo userData = new UserInfo("???????????????????????????????????????????????????????????????????????????????????????????????????"+i, random.nextInt(70), System.currentTimeMillis()
                    - random.nextInt(70)*3600*1000*24,true,new ChildData("??????"+i));
            userData.setUrl(tanBeans.get(i%urlSize).getUrl());
            testData.add(userData);
        }

        final Column<String> nameColumn = new Column<>("??????", "name");
        nameColumn.setFixed(true);
        nameColumn.setAutoCount(true);
        nameColumn.setDrawFormat(new MultiLineDrawFormat<String>(this,100));
        final Column<Integer> ageColumn = new Column<>("??????", "age");
        ageColumn.setFixed(true);
        ageColumn.setAutoCount(true);
        int imgSize = DensityUtils.dp2px(this,25);
        final Column<String> avatarColumn = new Column<>("??????", "url", new BitmapDrawFormat<String>(imgSize,imgSize) {
            @Override
            protected Bitmap getBitmap(final String s, String value, int position) {
                if(map.get(s)== null) {
                    Glide.with(MultParseModeActivity.this).asBitmap().load(s)
                            .apply(bitmapTransform(new CenterCrop())).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            map.put(s, bitmap);
                            table.invalidate();
                        }
                    });
                }
                return map.get(s);
            }
        });
        avatarColumn.setFixed(true);
       Column < String > column4 = new Column<>("??????????????????", "childData.child",new MultiLineDrawFormat<String>(this,100));
        column4.setAutoCount(true);
        final IFormat<Long> format =  new IFormat<Long>() {
            @Override
            public String format(Long aLong) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(aLong);
                return calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
            }
        };
        final Column<Long> timeColumn = new Column<>("??????", "time",format);
        timeColumn.setCountFormat(new ICountFormat<Long, Long>() {
            private long maxTime;
            @Override
            public void count(Long aLong) {
                if(aLong > maxTime){
                    maxTime = aLong;
                }
            }

            @Override
            public Long getCount() {
                return maxTime;
            }

            @Override
            public String getCountString() {
                return format.format(maxTime);
            }

            @Override
            public void clearCount() {
                maxTime =0;
            }
        });
        int size = DensityUtils.dp2px(this,15);
        Column<Boolean> column5 = new Column<>("??????1", "isCheck", new ImageResDrawFormat<Boolean>(size,size) {
            @Override
            protected Context getContext() {
                return MultParseModeActivity.this;
            }

            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.mipmap.check;
                }
                return 0;
            }
        });
        Column<Boolean> column6 = new Column<>("??????2", "isCheck", new TextImageDrawFormat<Boolean>(size,size, TextImageDrawFormat.LEFT,10) {
            @Override
            protected Context getContext() {
                return MultParseModeActivity.this;
            }

            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.mipmap.clock_fill;
                }
                return 0;
            }
        });
        Column<Boolean> column7 = new Column<>("??????3", "isCheck", new TextImageDrawFormat<Boolean>(size,size, TextImageDrawFormat.RIGHT,10) {
            @Override
            protected Context getContext() {
                return MultParseModeActivity.this;
            }

            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.mipmap.activity_fill;
                }
                return 0;
            }
        });
        Column<Boolean> column8 = new Column<>("??????4", "isCheck", new TextImageDrawFormat<Boolean>(size,size, TextImageDrawFormat.TOP,10) {
            @Override
            protected Context getContext() {
                return MultParseModeActivity.this;
            }

            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.mipmap.brush_fill;
                }
                return 0;
            }
        });
        Column<Boolean> column9 = new Column<>("??????5", "isCheck", new TextImageDrawFormat<Boolean>(size,size, TextImageDrawFormat.BOTTOM,10) {
            @Override
            protected Context getContext() {
                return MultParseModeActivity.this;
            }

            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.mipmap.collection_fill;
                }
                return 0;
            }
        });
        Column totalColumn1 = new Column("??????1",nameColumn,ageColumn);
        Column totalColumn2 = new Column("??????2",nameColumn,ageColumn,timeColumn);
        Column totalColumn = new Column("??????",nameColumn,totalColumn1,totalColumn2,timeColumn);

        final TableData<UserInfo> tableData = new TableData<>("??????",testData,nameColumn,
                avatarColumn,column4,column5,column6,column7,column8,column9,totalColumn,totalColumn1,totalColumn2,timeColumn);
        tableData.setShowCount(true);
        table.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
        table.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
        tableData.setTitleDrawFormat(new TitleImageDrawFormat(size,size, TitleImageDrawFormat.RIGHT,10) {
            @Override
            protected Context getContext() {
                return MultParseModeActivity.this;
            }

            @Override
            protected int getResourceID(Column column) {
                if(!column.isParent()){
                    if(tableData.getSortColumn() == column){
                        setDirection(TextImageDrawFormat.RIGHT);
                        if(column.isReverseSort()){
                            return R.mipmap.sort_up;
                        }
                        return R.mipmap.sort_down;

                    }else{
                        setDirection(TextImageDrawFormat.LEFT);
                        if(column == nameColumn){
                            return R.mipmap.name;
                        }else if(column == ageColumn){
                            return R.mipmap.age;
                        }else if(column == timeColumn){
                            return R.mipmap.update;
                        }
                    }
                    return 0;
                }
                setDirection(TextImageDrawFormat.LEFT);
                int level = tableData.getTableInfo().getMaxLevel()-column.getLevel();
                if(level ==0){
                    return R.mipmap.level1;
                }else if(level ==1){
                    return R.mipmap.level2;
                }
                return 0;
            }
        });
        ageColumn.setOnColumnItemClickListener(new OnColumnItemClickListener<Integer>() {
            @Override
            public void onClick(Column<Integer> column, String value, Integer integer, int position) {
                Toast.makeText(MultParseModeActivity.this,"?????????"+value,Toast.LENGTH_SHORT).show();
            }
        });
        FontStyle fontStyle = new FontStyle();
        fontStyle.setTextColor(getResources().getColor(android.R.color.white));
        MultiLineBubbleTip<Column> tip = new MultiLineBubbleTip<Column>(this,R.mipmap.round_rect,R.mipmap.triangle,fontStyle) {
            @Override
            public boolean isShowTip(Column column, int position) {
                if(column == nameColumn){
                    return true;
                }
                return false;
            }


            @Override
            public String[] format(Column column, int position) {
                UserInfo data = testData.get(position);
                String[] strings = {"??????","?????????"+data.getName().substring(0,10),data.getName().substring(10,20),"?????????"+data.getAge()};
                return strings;
            }
        };
        tip.setColorFilter(Color.parseColor("#FA8072"));
        tip.setAlpha(0.8f);
        table.getProvider().setTip(tip);

        table.setOnColumnClickListener(new OnColumnClickListener() {
            @Override
            public void onClick(ColumnInfo columnInfo) {
                if(!columnInfo.column.isParent()) {

                    if(columnInfo.column == ageColumn){
                        showChartDialog(tableData.getTableName(),nameColumn.getDatas(),ageColumn.getDatas());
                    }else{
                        table.setSortColumn(columnInfo.column, !columnInfo.column.isReverseSort());
                    }
                }
                Toast.makeText(MultParseModeActivity.this,"?????????"+columnInfo.column.getColumnName(),Toast.LENGTH_SHORT).show();
            }
        });
        table.getConfig().setTableTitleStyle(new FontStyle(this,15,getResources().getColor(R.color.arc1)));
        table.setTableData(tableData);

    }

    @Override
    public void onClick(View view) {
        changedStyle();
    }

    private void changedStyle() {

        if (chartDialog == null) {
            chartDialog = new BaseCheckDialog<>("????????????", new BaseCheckDialog.OnCheckChangeListener<TableStyle>() {
                @Override
                public String getItemText(TableStyle chartStyle) {
                    return chartStyle.value;
                }

                @Override
                public void onItemClick(TableStyle item, int position) {
                    switch (item) {
                        case FIXED_TITLE:
                            fixedTitle(item);
                            break;
                        case FIXED_X_AXIS:
                            fixedXAxis(item);
                            break;
                        case FIXED_Y_AXIS:
                           fixedYAxis(item);
                            break;
                        case FIXED_FIRST_COLUMN:
                            fixedFirstColumn(item);
                            break;
                        case FIXED_COUNT_ROW:
                            fixedCountRow(item);
                            break;
                        case ZOOM:
                            zoom(item);
                            break;

                    }
                }
            });
        }
        ArrayList<TableStyle> items = new ArrayList<>();

        items.add(TableStyle.FIXED_X_AXIS);
        items.add(TableStyle.FIXED_Y_AXIS);
        items.add(TableStyle.FIXED_TITLE);
        items.add(TableStyle.FIXED_FIRST_COLUMN);
        items.add(TableStyle.FIXED_COUNT_ROW);
        items.add(TableStyle.ZOOM);
        chartDialog.show(this, true, items);
    }

    private void zoom(TableStyle item) {
        quickChartDialog.showDialog(this, item, new String[]{"??????", "?????????"}, new QuickChartDialog.OnCheckChangeAdapter() {

            @Override
            public void onItemClick(String s, int position) {
                if (position == 0) {
                    table.setZoom(true,3,1);
                } else if (position == 1) {
                    table.setZoom(false,3,1);
                }
                table.invalidate();
            }
        });
    }

    private void fixedXAxis(TableStyle c) {

        quickChartDialog.showDialog(this, c, new String[]{"??????", "?????????"}, new QuickChartDialog.OnCheckChangeAdapter() {

            @Override
            public void onItemClick(String s, int position) {
                if (position == 0) {
                   table.getConfig().setFixedXSequence(true);
                } else if (position == 1) {
                    table.getConfig().setFixedXSequence(false);
                }
               table.invalidate();
            }
        });
    }

    private void fixedYAxis(TableStyle c) {

        quickChartDialog.showDialog(this, c, new String[]{"??????", "?????????"}, new QuickChartDialog.OnCheckChangeAdapter() {

            @Override
            public void onItemClick(String s, int position) {
                if (position == 0) {
                    table.getConfig().setFixedYSequence(true);
                } else if (position == 1) {
                    table.getConfig().setFixedYSequence(false);
                }
                table.invalidate();
            }
        });
    }
    private void fixedTitle(TableStyle c) {

        quickChartDialog.showDialog(this, c, new String[]{"??????", "?????????"}, new QuickChartDialog.OnCheckChangeAdapter() {

            @Override
            public void onItemClick(String s, int position) {
                if (position == 0) {
                    table.getConfig().setFixedTitle(true);
                } else if (position == 1) {
                    table.getConfig().setFixedTitle(false);
                }
                table.invalidate();
            }
        });
    }

    private void fixedFirstColumn(TableStyle c) {

        quickChartDialog.showDialog(this, c, new String[]{"??????", "?????????"}, new QuickChartDialog.OnCheckChangeAdapter() {

            @Override
            public void onItemClick(String s, int position) {
                if (position == 0) {
                    table.getConfig().setFixedFirstColumn(true);
                } else if (position == 1) {
                    table.getConfig().setFixedFirstColumn(false);
                }
                table.invalidate();
            }
        });
    }
    private void fixedCountRow(TableStyle c) {

        quickChartDialog.showDialog(this, c, new String[]{"??????", "?????????"}, new QuickChartDialog.OnCheckChangeAdapter() {

            @Override
            public void onItemClick(String s, int position) {
                if (position == 0) {
                    table.getConfig().setFixedCountRow(true);
                } else if (position == 1) {
                    table.getConfig().setFixedCountRow(false);
                }
                table.invalidate();
            }
        });
    }

    /**
     * ??????????????????????????????smartChart
     * @param tableName
     * @param chartYDataList
     * @param list
     */
    private void showChartDialog(String tableName,List<String> chartYDataList,List<Integer> list ){
        View chartView = View.inflate(this,R.layout.dialog_chart,null);
        LineChart lineChart = (LineChart) chartView.findViewById(R.id.lineChart);
        lineChart.setLineModel(LineChart.CURVE_MODEL);
        Resources res = getResources();
        com.daivd.chart.data.style.FontStyle.setDefaultTextSpSize(this,12);
        List<LineData> ColumnDatas = new ArrayList<>();
        ArrayList<Double> tempList1 = new ArrayList<>();
        ArrayList<String> ydataList = new ArrayList<>();
        for(int i = 0;i <30;i++){
            String value = chartYDataList.get(i);
            ydataList.add(value);
        }
        for(int i = 0;i <30;i++){
            int value = list.get(i);
            tempList1.add(Double.valueOf(value));
        }
        LineData columnData1 = new LineData(tableName,"", IAxis.AxisDirection.LEFT,getResources().getColor(R.color.arc1),tempList1);
        ColumnDatas.add(columnData1);
        ChartData<LineData> chartData2 = new ChartData<>("Area Chart",ydataList,ColumnDatas);
        lineChart.getChartTitle().setDirection(IComponent.TOP);
        lineChart.getLegend().setDirection(IComponent.BOTTOM);
        lineChart.setLineModel(LineChart.CURVE_MODEL);
        BaseAxis verticalAxis =  lineChart.getLeftVerticalAxis();
        BaseAxis horizontalAxis=  lineChart.getHorizontalAxis();
        //??????????????????
        verticalAxis.setAxisDirection(IAxis.AxisDirection.LEFT);
        //????????????
        verticalAxis.setDrawGrid(true);
        //??????????????????
        horizontalAxis.setAxisDirection(IAxis.AxisDirection.BOTTOM);
        horizontalAxis.setDrawGrid(true);
        //??????????????????
        verticalAxis.getAxisStyle().setWidth(this,1);
        DashPathEffect effects = new DashPathEffect(new float[] { 1, 2, 4, 8}, 1);
        verticalAxis.getGridStyle().setWidth(this,1).setColor(res.getColor(R.color.arc_text)).setEffect(effects);
        horizontalAxis.getGridStyle().setWidth(this,1).setColor(res.getColor(R.color.arc_text)).setEffect(effects);
        lineChart.setZoom(true);
        //???????????????
        lineChart.getProvider().setOpenCross(true);
        lineChart.getProvider().setCross(new VerticalCross());
        lineChart.getProvider().setShowText(true);
        //??????MarkView
        lineChart.getProvider().setOpenMark(true);
        //??????MarkView
        lineChart.getProvider().setMarkView(new BubbleMarkView(this));

        //??????????????????
        lineChart.setShowChartName(true);
        //??????????????????
        com.daivd.chart.data.style.FontStyle fontStyle = lineChart.getChartTitle().getFontStyle();
        fontStyle.setTextColor(res.getColor(R.color.arc_temp));
        fontStyle.setTextSpSize(this,15);
        LevelLine levelLine = new LevelLine(30);
        DashPathEffect effects2 = new DashPathEffect(new float[] { 1, 2,2,4}, 1);
        levelLine.getLineStyle().setWidth(this,1).setColor(res.getColor(R.color.arc23)).setEffect(effects);
        levelLine.getLineStyle().setEffect(effects2);
        lineChart.getProvider().addLevelLine(levelLine);
        Point legendPoint = (Point) lineChart.getLegend().getPoint();
        PointStyle style = legendPoint.getPointStyle();
        style.setShape(PointStyle.SQUARE);
        lineChart.getProvider().setArea(true);
        lineChart.getHorizontalAxis().setRotateAngle(90);
        lineChart.setChartData(chartData2);
        lineChart.startChartAnim(400);
        BaseDialog dialog = new  BaseDialog.Builder(this).setFillWidth(true).setContentView(chartView).create();
        dialog.show();
    }

}
