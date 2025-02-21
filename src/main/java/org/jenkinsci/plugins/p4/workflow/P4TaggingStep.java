package org.jenkinsci.plugins.p4.workflow;

import com.google.common.collect.ImmutableSet;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.p4.tagging.TagNotifierStep;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Set;

public class P4TaggingStep extends Step {

	private final String rawLabelName;
	private final String rawLabelDesc;

	@DataBoundConstructor
	public P4TaggingStep(String rawLabelName, String rawLabelDesc) {
		this.rawLabelName = rawLabelName;
		this.rawLabelDesc = rawLabelDesc;
	}

	public String getRawLabelName() {
		return rawLabelName;
	}

	public String getRawLabelDesc() {
		return rawLabelDesc;
	}

	@Override
	public StepExecution start(StepContext context) {
		return new P4TaggingStepExecution(context, this);
	}

	@Extension(optional = true)
	@Symbol("label")
	public static final class DescriptorImpl extends StepDescriptor {

		@Override
		public Set<? extends Class<?>> getRequiredContext() {
			return ImmutableSet.of(Run.class, FilePath.class, TaskListener.class, EnvVars.class);
		}

		@Override
		public String getFunctionName() {
			return "p4tag";
		}

		@Override
		public String getDisplayName() {
			return "P4 Tag";
		}

	}

	public static class P4TaggingStepExecution extends SynchronousStepExecution<Void> {

		private static final long serialVersionUID = 1L;

		private final transient P4TaggingStep step;

		protected P4TaggingStepExecution(@Nonnull StepContext context, P4TaggingStep step) {
			super(context);
			this.step = step;
		}

		@Override
		protected Void run() throws Exception {
			TagNotifierStep notifier = new TagNotifierStep(step.getRawLabelName(), step.getRawLabelDesc(), false);
			notifier.perform(getContext().get(Run.class), getContext().get(FilePath.class), getContext().get(Launcher.class), getContext().get(TaskListener.class));
			return null;
		}
	}
}
