package net.bither.bitherj.db;

import net.bither.bitherj.core.EnterpriseHDMAddress;
import net.bither.bitherj.core.EnterpriseHDMKeychain;

import java.util.List;

public interface IEnterpriseHDMProvider {


    String getEnterpriseEncryptMnemonicSeed(int hdSeedId);

    String getEnterpriseEncryptHDSeed(int hdSeedId);

    String getEnterpriseHDFristAddress(int hdSeedId);

    boolean isEnterpriseHDMSeedFromXRandom(int hdSeedId);

    void addEnterpriseHDMAddress(List<EnterpriseHDMAddress> enterpriseHDMAddressList);

    List<EnterpriseHDMAddress> getEnterpriseHDMAddress(EnterpriseHDMKeychain keychain);

    void addMultiSignSet(int n, int m);

    void updateSyncComplete(EnterpriseHDMAddress enterpriseHDMAddress);

    List<Integer> getEnterpriseHDMKeychainIds();

    int getEnterpriseHDMSeedId();

    int getPubCount();

    int getThreshold();
}

package de.odysseus.staxon.json;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;


public class JsonXMLConfigBuilder {
	protected final JsonXMLConfigImpl config;
	
	
	public JsonXMLConfigBuilder() {
		this(new JsonXMLConfigImpl());
	}
	
	protected JsonXMLConfigBuilder(JsonXMLConfigImpl config) {
		this.config = config;
	}
	
	
	public JsonXMLConfig build() {
		return config.clone();
	}
	
	
	public JsonXMLConfigBuilder autoArray(boolean autoArray) {
		config.setAutoArray(autoArray);
		return this;
	}

	
	public JsonXMLConfigBuilder autoPrimitive(boolean autoPrimitive) {
		config.setAutoPrimitive(autoPrimitive);
		return this;
	}

	
	public JsonXMLConfigBuilder multiplePI(boolean multiplePI) {
		config.setMultiplePI(multiplePI);
		return this;
	}

	
	public JsonXMLConfigBuilder namespaceDeclarations(boolean namespaceDeclarations) {
		config.setNamespaceDeclarations(namespaceDeclarations);
		return this;
	}
	
	
	public JsonXMLConfigBuilder namespaceSeparator(char namespaceSeparator) {
		config.setNamespaceSeparator(namespaceSeparator);
		return this;
	}
	
	
	public JsonXMLConfigBuilder prettyPrint(boolean prettyPrint) {
		config.setPrettyPrint(prettyPrint);
		return this;
	}

	
	public JsonXMLConfigBuilder virtualRoot(QName virtualRoot) {
		config.setVirtualRoot(virtualRoot);
		return this;
	}
	
	
	public JsonXMLConfigBuilder virtualRoot(String virtualRoot) {
		config.setVirtualRoot(QName.valueOf(virtualRoot));
		return this;
	}

	
	public JsonXMLConfigBuilder repairingNamespaces(boolean repairingNamespaces) {
		config.setRepairingNamespaces(repairingNamespaces);
		return this;
	}

	
	public JsonXMLConfigBuilder namespaceMappings(Map<String, String> namespaceMappings) {
		config.setNamespaceMappings(namespaceMappings);
		return this;
	}

	
	public JsonXMLConfigBuilder namespaceMapping(String prefix, String namespaceURI) {
		Map<String, String> namespaceMappings = new HashMap<String, String>();
		if (config.getNamespaceMappings() != null) {
			namespaceMappings.putAll(config.getNamespaceMappings());
		}
		namespaceMappings.put(prefix, namespaceURI);
		config.setNamespaceMappings(namespaceMappings);
		return this;
	}

	
	public JsonXMLConfigBuilder textProperty(String textProperty) {
		config.setTextProperty(textProperty);
		return this;
	}

	
	public JsonXMLConfigBuilder attributePrefix(String attributePrefix) {
		config.setAttributePrefix(attributePrefix);
		return this;
	}
}