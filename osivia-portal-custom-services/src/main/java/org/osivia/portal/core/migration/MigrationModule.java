package org.osivia.portal.core.migration;

public abstract class MigrationModule {
  public abstract long getModuleId();
  public abstract void execute ();
}
