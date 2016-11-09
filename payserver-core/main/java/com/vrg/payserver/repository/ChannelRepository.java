/**
 *
 */
package com.vrg.payserver.repository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vrg.payserver.service.IChannel;
import com.vrg.payserver.service.IChannelFactory;
import com.vrg.payserver.service.IServerCoreService;
import com.vrg.payserver.util.Log;

@Service
public class ChannelRepository {
    public static final String CLASS_PATTERN = "com.xgsdk.sdkserver.impl.ChannelFactory";
    private static final String KEY_PATTERN = "%s__%s";

    private ConcurrentHashMap<String, IChannel> channels = new ConcurrentHashMap<String, IChannel>();
    private ConcurrentHashMap<String, ChannelImplementObject> implementObjectMap = new ConcurrentHashMap<>();

    @Value("${paysdk.channelimpljar.location.pattern}")
    private String channelImplJarLocationPattern;

    @Autowired
    private IServerCoreService serverCoreService;

    public IChannel getChannelImpl(String channelId) {
        if (!implementObjectMap.containsKey(channelId)) {
            syncLoadChanelImplement(channelId);
        }
        String key = String.format(KEY_PATTERN, channelId);
        return channels.get(key);
    }

    private void syncLoadChanelImplement(String channelId) {
        synchronized (channelId.intern()) {
            if (!implementObjectMap.containsKey(channelId)) {
                loadChannelImplement(channelId);
            }
        }
    }

    public void checkUpdate() {
        for (Entry<String, ChannelImplementObject> entry : implementObjectMap.entrySet()) {
            ChannelImplementObject implementObject = entry.getValue();
            String channelId = implementObject.getChannelId();
            long fileLength = implementObject.getFileLength();
            long lastModified = implementObject.getLastModified();
            File file = new File(MessageFormat.format(channelImplJarLocationPattern, channelId));
            if (file.exists() && (fileLength != file.length() || lastModified != file.lastModified())) {
                Log.supplementMessage(MessageFormat.format("begin to load channel implement object, channelId={0}", channelId));
                loadChannelImplement(channelId);
                Log.supplementMessage(MessageFormat.format("The channel implement object has been updated, channelId={0}", channelId));
            }
        }
    }

    private void loadChannelImplement(String channelId) {
        if (!implementObjectMap.containsKey(channelId)) {

        }

        File file = new File(MessageFormat.format(channelImplJarLocationPattern, channelId));
        if (!file.exists()) {
          Log.supplementMessage(MessageFormat.format("can not find channel implement jar, fileName={0},channelId={1}", file.getAbsolutePath(),
                    channelId));
            try {
                // 从系统Classloader中查找，方便开发调测
                Class<?> clazz = Class.forName(CLASS_PATTERN);
                createChannelImpl(channelId, clazz);
                Log.supplementMessage(MessageFormat.format("load channel implement from system, channelId={0}", channelId));
            } catch (Throwable e) {
                Log.supplementExceptionMessage(e);
            }
            return;
        }

        try {
            Log.supplementMessage(MessageFormat.format("start to load channel implement, fileName={0},channelId={1}", file.getAbsolutePath(), channelId));
            URLClassLoader loader = new URLClassLoader(new URL[] { file.toURI().toURL() },
                    IChannelFactory.class.getClassLoader());
            Class<?> clazz = loader.loadClass(CLASS_PATTERN);
            if (!IChannelFactory.class.isAssignableFrom(clazz)) {
                Log.supplementMessage(MessageFormat.format("Channel factory class type is not correct,fileName={0},channelId={1}",
                        file.getAbsolutePath(), channelId));
                loader.close();
                return;
            }
            createChannelImpl(channelId, clazz);
            saveJarInfo(channelId, file, loader);
            Log.supplementMessage(MessageFormat.format("end to load channel implement, fileName={0},channelId={1}", file.getAbsolutePath(), channelId));
        } catch (Throwable t) {
            Log.supplementExceptionMessage(t);
        }
    }

    private void createChannelImpl(String channelId, Class<?> clazz)
            throws InstantiationException, IllegalAccessException {
        Object object = clazz.newInstance();
        IChannelFactory channelFactory = (IChannelFactory) object;
        Map<String, IChannel> channelImpls = channelFactory.createChannelWithVersions();
        for (Entry<String, IChannel> entry : channelImpls.entrySet()) {
            String key = String.format(KEY_PATTERN, channelId, entry.getKey());
            IChannel channel = entry.getValue();
            channel.setServerCoreService(serverCoreService);
            channels.put(key, channel);
        }
    }

    private void saveJarInfo(String channelId, File file, URLClassLoader loader) throws IOException {
        ChannelImplementObject existingImplementObject = implementObjectMap.get(channelId);
        if (existingImplementObject != null && existingImplementObject.getUrlClassLoader() != null) {
            URLClassLoader oldClassLoader = existingImplementObject.getUrlClassLoader();
            oldClassLoader.close();
            Log.supplementMessage("Closed URLClassLoader" + oldClassLoader + " for channel " + channelId + ", created a new one "
                    + loader);
        }
        ChannelImplementObject implementObject = new ChannelImplementObject();
        implementObject.setFileLength(file.length());
        implementObject.setLastModified(file.lastModified());
        implementObject.setChannelId(channelId);
        implementObject.setUrlClassLoader(loader);
        implementObjectMap.put(channelId, implementObject);
    }
}
