package io.github.douira.glsl_transformer.core;

import io.github.douira.glsl_transformer.core.target.*;
import io.github.douira.glsl_transformer.transform.*;

/**
 * The dynamic wrap identifier transformation has a number of abstract methods
 * that can be implemented in order to dynamically supply the parameters for
 * this identifier wrap operation. The wrapping injector is given directly
 * though since the injection methods can vary greatly.
 * 
 * TODO: add test that the replacer doesn't run on the injected code (i.e. that
 * all three steps are sequential)
 */
public abstract class WrapIdentifierDynamic<T extends JobParameters> extends Transformation<T> {
  /**
   * Creates a new wrap identifier transformation with a specified wrapping
   * injector phase.
   * 
   * @param wrappingInjector A phase that injects the additional code into the
   *                         tree required for making the wrap work
   */
  public WrapIdentifierDynamic(TransformationPhase<T> wrappingInjector) {
    this();
    chainDependent(wrappingInjector);
  }

  /**
   * Creates a new wrap identifier transformation with no wrapping injector. The
   * phase for that should be added to the transformation manually.
   */
  protected WrapIdentifierDynamic() {
    addEndDependent(new SearchTerminalsImpl<T>(new WrapThrowTarget<T>() {
      @Override
      protected String getWrapResult() {
        return getWrapResultDynamic();
      }
    }) {
      @Override
      protected boolean isActive() {
        return isActiveDynamic();
      }
    });

    chainDependent(new SearchTerminalsImpl<T>(new TerminalReplaceTarget<T>() {
      @Override
      protected String getTerminalContent() {
        return getWrapResultDynamic();
      }

      @Override
      public String getNeedle() {
        return getWrapTargetDynamic();
      }
    }) {
      @Override
      protected boolean isActive() {
        return isActiveDynamic();
      }
    });
  }

  /**
   * Returns if the phases should be active.
   * 
   * @return {@code true} if the phases should be active
   */
  protected abstract boolean isActiveDynamic();

  /**
   * Returns the wrapping result that is used as a replacement for the target.
   * 
   * @return The wrapping result
   */
  protected abstract String getWrapResultDynamic();

  /**
   * Returns the wrapping target. The wrapping target is the identifier that is
   * replaced with the wrap result.
   * 
   * @return The wrapping target
   */
  protected abstract String getWrapTargetDynamic();
}
