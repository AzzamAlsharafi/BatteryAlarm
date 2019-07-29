package multiplies.batteryalarm.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import multiplies.batteryalarm.R;
import multiplies.batteryalarm.activity.MainActivity;

public class SetPercentageDialog extends DialogFragment {

    SharedPreferences preferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_set_percentage, null);
        EditText editText = view.findViewById(R.id.editText);
        int percentage = preferences.getInt(MainActivity.ALARM_PERCENTAGE_KEY, -1);
        editText.setHint(percentage == -1 ? getString(R.string.empty) : Integer.toString(percentage));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()){
                    int value = Integer.parseInt(editable.toString());
                    if(value > 100){
                        editText.setError(getString(R.string.edit_text_100_error));
                        editText.setText(R.string._100);
                    }
                }
            }
        });

        builder.setView(view);

        builder.setTitle(R.string.set_percentage_dialog_title);
        builder.setPositiveButton(R.string.positive_button_text, (dialogInterface, i) -> {
            if(!editText.getText().toString().isEmpty()){
                editor.putInt(MainActivity.ALARM_PERCENTAGE_KEY, Integer.parseInt(editText.getText().toString()));
                editor.commit();
            }
            dialogInterface.cancel();
        });
        builder.setNegativeButton(R.string.negative_button_text, (dialogInterface, i) -> dialogInterface.cancel());
        return builder.create();
    }
}
