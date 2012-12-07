package com.btmatthews.maven.plugins.inmemdb.mojo;

import com.btmatthews.utils.monitor.mojo.AbstractStopMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Stop an in-memory database server that is running as a daemon.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
@Mojo(name = "stop", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class StopMojo extends AbstractStopMojo {
}
