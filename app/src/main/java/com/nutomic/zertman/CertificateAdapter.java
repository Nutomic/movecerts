package com.nutomic.zertman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * List Adapter for {@link Certificate}.
 */
public class CertificateAdapter extends ArrayAdapter<Certificate> {

	public CertificateAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}

		TextView title = (TextView) convertView.findViewById(android.R.id.text1);
		title.setText(getItem(position).getFile().getName());
		convertView.setBackgroundColor(getContext().getResources().getColor(
				(getItem(position).isSystemCertificate())
						? R.color.background_system_certificate
						: R.color.background_user_certificate));
		return convertView;
	}

}
