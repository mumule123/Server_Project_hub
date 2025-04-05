package jakarta.faces.flow;


public abstract class Parameter
{
    public abstract String getName();
    
    public abstract jakarta.el.ValueExpression getValue();
    
}
package org.remast.baralga.model.report;

import java.util.Collection;
import java.util.List;
import java.util.Observable;

import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

public class AccumulatedActivitiesReport extends Observable {

    
    private Collection<ProjectActivity> data;

    
    private SortedList<AccumulatedProjectActivity> accumulatedActivitiesByDay;

    
    protected Filter filter;

    
    public AccumulatedActivitiesReport(final Collection<ProjectActivity> data, Filter filter) {
        this.data = data;
        this.filter = filter;
        this.accumulatedActivitiesByDay = new SortedList<>(new BasicEventList<>());

        accumulate();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        
        for (AccumulatedProjectActivity activity : accumulatedActivitiesByDay) {
            result.append(activity.toString()).append(":"); 
        }

        return "[" + result.toString() + "]"; 
    }

    
    public void acummulateActivity(final ProjectActivity activity) {
        AccumulatedProjectActivity newAccActivity = new AccumulatedProjectActivity(activity.getProject(), activity
                .getStart(), activity.getDuration());
        if (this.accumulatedActivitiesByDay.contains(newAccActivity)) {
            final AccumulatedProjectActivity accActivity = this.accumulatedActivitiesByDay.get(accumulatedActivitiesByDay.indexOf(newAccActivity));
            accActivity.addTime(newAccActivity.getTime());
        } else {
            this.accumulatedActivitiesByDay.add(newAccActivity);
        }
    }

    
    protected void accumulate() {
        this.accumulatedActivitiesByDay.clear();

        for (ProjectActivity activity : this.data) {
            this.acummulateActivity(activity);
        }
    }

    
    public SortedList<AccumulatedProjectActivity> getAccumulatedActivitiesByDay() {
        return accumulatedActivitiesByDay;
    }

    
    public Filter getFilter() {
        return filter;
    }

    
    public void setFilter(final Filter filter) {
        this.filter = filter;
        accumulate();
    }

    
    public void setData(final List<ProjectActivity> data) {
        this.data = data;
        accumulate();
    }
}