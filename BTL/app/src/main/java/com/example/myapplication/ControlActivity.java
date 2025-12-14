package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ControlActivity extends AppCompatActivity {

    private TextView tvTempVal, tvHumidVal, tvTimeVal;
    private CardView cardTemp, cardHumid, cardTime;
    private Spinner spinnerEggType;
    private Button btnFinishSetup;

    private View btnPower;
    private TextView tvPowerStatus;
    private View layoutContent, layoutContentBottom;
    private boolean isSystemOn = false;
    private ImageButton backBtn;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private
    static class EggParams {
        double temp;
        int humid;
        int days;
        public EggParams(double temp, int humid, int days) {
            this.temp = temp;
            this.humid = humid;
            this.days = days;
        }
    }

    static class EggItem {
        String name;
        String details;
        public EggItem(String name, String details) {
            this.name = name;
            this.details = details;
        }
    }

    private Map<String, EggParams> eggDatabase = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("esp");

        mapViews();
        initData();
        setupUI();
        setupSpinner();
        setupEvents();
        backBtn = (ImageButton)findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(ControlActivity    .this, NavActivity.class));
            finish();
        });
        GetData();
        updateSystemState(isSystemOn);
    }

    private void GetData() {
        myRef.child("system").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) return;

                Object tempObj = snapshot.child("temp").getValue();
                Object humidObj = snapshot.child("humid").getValue();
                Object daysObj = snapshot.child("days").getValue();
                Object eggTypeObj = snapshot.child("eggType").getValue();
                Boolean power = snapshot.child("power").getValue(Boolean.class);

                if (tempObj != null) tvTempVal.setText(String.valueOf(tempObj));
                if (humidObj != null) tvHumidVal.setText(String.valueOf(humidObj));
                if (daysObj != null) tvTimeVal.setText(String.valueOf(daysObj));
                if (eggTypeObj != null) selectEggInSpinner(eggTypeObj.toString());
                if (power != null) {
                    isSystemOn = power;
                    updateSystemState(isSystemOn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void writeToFirebase() {

        String temp = tvTempVal.getText().toString();
        String humid = tvHumidVal.getText().toString();
        String days = tvTimeVal.getText().toString();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("temp", temp);
        data.put("humid", humid);
        data.put("days", days);
        data.put("power", isSystemOn);

        myRef.child("system").setValue(data)
                .addOnSuccessListener(a -> Toast.makeText(this, "Đã gửi dữ liệu!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Gửi thất bại!", Toast.LENGTH_SHORT).show());
    }

    private void initData() {
        eggDatabase.put("Gà", new EggParams(37.5, 60, 21));
        eggDatabase.put("Vịt", new EggParams(37.8, 65, 28));
        eggDatabase.put("Ngan", new EggParams(37.6, 70, 30));
        eggDatabase.put("Chim cút", new EggParams(37.2, 55, 17));
        eggDatabase.put("Tự thiết lập", new EggParams(0.0, 0, 0));
    }

    private void mapViews() {
        cardTemp = findViewById(R.id.cardTemp);
        cardHumid = findViewById(R.id.cardHumid);
        cardTime = findViewById(R.id.cardTime);

        tvTempVal = cardTemp.findViewById(R.id.tvValue);
        tvHumidVal = cardHumid.findViewById(R.id.tvValue);
        tvTimeVal = cardTime.findViewById(R.id.tvValue);

        spinnerEggType = findViewById(R.id.spinnerEggType);
        btnFinishSetup = findViewById(R.id.btnFinishSetup);

        btnPower = findViewById(R.id.btnPower);
        tvPowerStatus = findViewById(R.id.tvPowerStatus);
        layoutContent = findViewById(R.id.layoutContent);
        layoutContentBottom = findViewById(R.id.layoutContentBottom);
    }

    private void setupUI() {
        ImageView imgTemp = cardTemp.findViewById(R.id.imgIcon);
        TextView lblTemp = cardTemp.findViewById(R.id.tvLabel);
        imgTemp.setImageResource(R.drawable.icon_nhiet_do);
        lblTemp.setText("Nhiệt độ (°C)");

        ImageView imgHumid = cardHumid.findViewById(R.id.imgIcon);
        TextView lblHumid = cardHumid.findViewById(R.id.tvLabel);
        imgHumid.setImageResource(R.drawable.icon_do_am);
        lblHumid.setText("Độ ẩm (%)");

        ImageView imgTime = cardTime.findViewById(R.id.imgIcon);
        TextView lblTime = cardTime.findViewById(R.id.tvLabel);
        imgTime.setImageResource(R.drawable.icon_thoi_gian);
        lblTime.setText("Thời gian (Ngày)");
    }

    private void setupSpinner() {
        List<EggItem> eggList = new ArrayList<>();

        for (String key : eggDatabase.keySet()) {
            EggParams params = eggDatabase.get(key);
            String detailText;

            if (key.equals("Tự thiết lập")) {
                detailText = "Tự nhập thông số thủ công";
            } else {
                detailText = params.days + " ngày • " + params.temp + "°C • " + params.humid + "%";
            }

            String displayName = (key.startsWith("Trứng") || key.equals("Tự thiết lập")) ? key : "Trứng " + key;
            eggList.add(new EggItem(displayName, detailText));
        }

        EggAdapter adapter = new EggAdapter(this, eggList);
        spinnerEggType.setAdapter(adapter);

        spinnerEggType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSystemOn) return;

                EggItem item = (EggItem) parent.getItemAtPosition(position);
                myRef.child("system").child("eggType").setValue(item.name);

                String keyLookup = item.name.replace("Trứng ", "");

                EggParams params = eggDatabase.get(keyLookup);
                if (params == null) params = eggDatabase.get(item.name);

                if (item.name.equals("Tự thiết lập")) {
                    setInputState(true);
                } else {
                    if (params != null) {
                        tvTempVal.setText(String.valueOf(params.temp));
                        tvHumidVal.setText(String.valueOf(params.humid));
                        tvTimeVal.setText(String.valueOf(params.days));
                    }
                    setInputState(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setInputState(boolean isEditable) {
        cardTemp.setEnabled(isEditable);
        cardHumid.setEnabled(isEditable);
        cardTime.setEnabled(isEditable);

        float alpha = isEditable ? 1.0f : 0.6f;
        cardTemp.setAlpha(alpha);
        cardHumid.setAlpha(alpha);
        cardTime.setAlpha(alpha);
    }

    private void setupEvents() {
        btnPower.setOnClickListener(v -> {
            isSystemOn = !isSystemOn;
            updateSystemState(isSystemOn);
        });

        cardTemp.setOnClickListener(v -> showAdjustDialog("Nhiệt độ", tvTempVal, 0.1));
        cardHumid.setOnClickListener(v -> showAdjustDialog("Độ ẩm", tvHumidVal, 1.0));
        cardTime.setOnClickListener(v -> showAdjustDialog("Thời gian ấp", tvTimeVal, 1.0));

        btnFinishSetup.setOnClickListener(v -> {
            writeToFirebase();
            showSuccessPopup();
        });
    }

    private void updateSystemState(boolean isOn) {
        if (isOn) {
            tvPowerStatus.setText("TẮT");
            tvPowerStatus.setTextColor(Color.parseColor("#555555"));

            layoutContent.setAlpha(1.0f);
            layoutContentBottom.setAlpha(1.0f);
            spinnerEggType.setEnabled(true);
            btnFinishSetup.setEnabled(true);

            if (spinnerEggType.getSelectedItem() != null) {
                EggItem item = (EggItem) spinnerEggType.getSelectedItem();
                boolean isCustom = item.name.equals("Tự thiết lập");
                setInputState(isCustom);
            }
        } else {
            tvPowerStatus.setText("BẬT");
            tvPowerStatus.setTextColor(Color.parseColor("#4CAF50"));

            layoutContent.setAlpha(0.3f);
            layoutContentBottom.setAlpha(0.3f);

            spinnerEggType.setEnabled(false);
            btnFinishSetup.setEnabled(false);
            setInputState(false);
        }
    }

    private void showAdjustDialog(String title, final TextView targetTextView, final double step) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_adjust, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        final EditText edtValue = dialogView.findViewById(R.id.edtValue);
        ImageView btnMinus = dialogView.findViewById(R.id.btnMinus);
        ImageView btnPlus = dialogView.findViewById(R.id.btnPlus);
        Button btnDone = dialogView.findViewById(R.id.btnDoneAdjust);

        tvTitle.setText("Điều chỉnh " + title);
        edtValue.setText(targetTextView.getText().toString());

        btnMinus.setOnClickListener(v -> {
            double current = parseDoubleSafe(edtValue.getText().toString());
            double newValue = current - step;
            if (newValue < 0) newValue = 0;

            if (step == 1.0) edtValue.setText(String.valueOf((int) newValue));
            else edtValue.setText(String.format("%.1f", newValue));
        });

        btnPlus.setOnClickListener(v -> {
            double current = parseDoubleSafe(edtValue.getText().toString());
            double newValue = current + step;
            if (newValue > 100) newValue = 100;

            if (step == 1.0) edtValue.setText(String.valueOf((int) newValue));
            else edtValue.setText(String.format("%.1f", newValue));
        });

        btnDone.setOnClickListener(v -> {
            String rawValue = edtValue.getText().toString();
            double finalVal = parseDoubleSafe(rawValue);

            if (finalVal > 100) finalVal = 100;
            else if (finalVal < 0) finalVal = 0;

            if (step == 1.0) targetTextView.setText(String.valueOf((int) finalVal));
            else targetTextView.setText(String.format("%.1f", finalVal));

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showSuccessPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Đã thiết lập thông số ấp trứng thành công!");
        builder.setCancelable(false);
        builder.setPositiveButton("Xong", (dialog, which) -> {
            Toast.makeText(ControlActivity.this, "Đang khởi động máy ấp...", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Thiết lập lại", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public class EggAdapter extends ArrayAdapter<EggItem> {
        public EggAdapter(Context context, List<EggItem> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        private View initView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_egg, parent, false);
            }
            EggItem currentItem = getItem(position);
            TextView tvName = convertView.findViewById(R.id.tvEggName);
            TextView tvDetails = convertView.findViewById(R.id.tvEggDetails);

            if (currentItem != null) {
                tvName.setText(currentItem.name);
                tvDetails.setText(currentItem.details);
            }
            return convertView;
        }
    }
    private void selectEggInSpinner(String eggName) {
        for (int i = 0; i < spinnerEggType.getCount(); i++) {
            EggItem item = (EggItem) spinnerEggType.getItemAtPosition(i);
            if (item.name.equals(eggName)) {
                spinnerEggType.setSelection(i);
                break;
            }
        }
    }

}
