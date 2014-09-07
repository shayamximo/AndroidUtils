package com.soa.bhc.comm;

public class CommnunicationExecutor {

	private CommnunicationExecutor commnunicationExecutor;

	private CommnunicationExecutor() {
	}

	public CommnunicationExecutor getInstance() {
		if (commnunicationExecutor == null) {
			commnunicationExecutor = new CommnunicationExecutor();
		}
		return commnunicationExecutor;
	}

}
