package com.amnesty.panicbutton.sms;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.amnesty.panicbutton.R;
import com.amnesty.panicbutton.model.SMSSettings;
import com.amnesty.panicbutton.wizard.NestedFragment;
import com.amnesty.panicbutton.wizard.WizardAction;

import java.util.ArrayList;
import java.util.List;

import static com.amnesty.panicbutton.R.id.*;

public class SMSSettingsFragment extends NestedFragment {
    private EditText firstContact;
    private EditText secondContact;
    private EditText thirdContact;
    private EditText smsEditText;
    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.sms_settings_fragment, container, false);
        SMSSettings currentSettings = SMSSettings.retrieve(context);
        initializeViews();
        displaySettings(currentSettings);
        return inflate;
    }

    private void displaySettings(SMSSettings settings) {
        smsEditText.setText(settings.message());
        firstContact.setText(settings.maskedPhoneNumberAt(0));
        secondContact.setText(settings.maskedPhoneNumberAt(1));
        thirdContact.setText(settings.maskedPhoneNumberAt(2));
    }

    private void initializeViews() {
        firstContact = (EditText) findViewInFragmentById(R.id.first_contact, R.id.contact_edit_text);
        secondContact = (EditText) findViewInFragmentById(R.id.second_contact, R.id.contact_edit_text);
        thirdContact = (EditText) findViewInFragmentById(R.id.third_contact, R.id.contact_edit_text);
        smsEditText = (EditText) findViewInFragmentById(R.id.sms_message, R.id.message_edit_text);
    }

    private View findViewInFragmentById(int fragmentId, int viewId) {
        Fragment fragment = getFragmentManager().findFragmentById(fragmentId);
        return fragment.getView().findViewById(viewId);
    }

    @Override
    protected int[] getFragmentIds() {
        return new int[]{first_contact, second_contact, third_contact, sms_message};
    }

    @Override
    public String action() {
        return getString(WizardAction.SAVE.actionId());
    }

    @Override
    public void performAction() {
        String message = smsEditText.getText().toString();
        SMSSettings currentSMSSettings = SMSSettings.retrieve(context);

        List<String> phoneNumbers = getPhoneNumbersFromView(currentSMSSettings);
        SMSSettings newSMSSettings = new SMSSettings(phoneNumbers, message);

        SMSSettings.save(context, newSMSSettings);
        Toast.makeText(context, R.string.successfully_saved, Toast.LENGTH_LONG).show();
        displaySettings(newSMSSettings);
    }

    private List<String> getPhoneNumbersFromView(SMSSettings currentSMSSettings) {
        currentSMSSettings.maskedPhoneNumberAt(0);

        List<String> phoneNumbers = new ArrayList<String>();
        phoneNumbers.add(getPhoneNumber(currentSMSSettings, 0, firstContact.getText().toString()));
        phoneNumbers.add(getPhoneNumber(currentSMSSettings, 1, secondContact.getText().toString()));
        phoneNumbers.add(getPhoneNumber(currentSMSSettings, 2, thirdContact.getText().toString()));

        return phoneNumbers;
    }

    private String getPhoneNumber(SMSSettings currentSMSSettings, int index, String contactNumberInView) {
        if (currentSMSSettings.maskedPhoneNumberAt(index).equals(contactNumberInView)) {
            return currentSMSSettings.phoneNumberAt(index);
        }
        return contactNumberInView;
    }
}