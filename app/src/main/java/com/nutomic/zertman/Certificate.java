package com.nutomic.zertman;

import java.io.File;
import java.util.Arrays;

public class Certificate {

	private final String mFilename;

	private final boolean mIsSystemCertificate;

	public Certificate(String filename, boolean isSystemCertificate) {
		mFilename = filename;
		mIsSystemCertificate = isSystemCertificate;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Certificate))
			return false;

		return mFilename.equals(((Certificate) o).mFilename) &&
				mIsSystemCertificate == ((Certificate) o).mIsSystemCertificate;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{mFilename, mIsSystemCertificate});
	}

	public File getFile() {
		return (mIsSystemCertificate)
				? new File(CertificateManager.SYSTEM_CERTIFICATES_DIR, mFilename)
				: new File(CertificateManager.getUserCertificatesDir(), mFilename);
	}
	public boolean isSystemCertificate() {
		return mIsSystemCertificate;
	}

}
