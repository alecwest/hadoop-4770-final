import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class HadoopMain
{
	static final int regression_sz = 5;
	
	public static void main(String[] args) throws IOException
	{
		Scanner yearly_avgs = new Scanner(new BufferedReader(new FileReader("/home/hadoop/output/part-r-00000")));
		String line_parse;
		ArrayList<Double> year = new ArrayList<Double>();
		ArrayList<Double> avg_temp = new ArrayList<Double>();
		LinearRegression lin_reg = null;
		Integer i, year_sz, avg_temp_sz;

		while (yearly_avgs.hasNext())
		{
			line_parse = yearly_avgs.next();
			year.add(Double.parseDouble(line_parse));
			line_parse = yearly_avgs.next();
			avg_temp.add(Double.parseDouble(line_parse));
		}

		if (year.size() != avg_temp.size())
		{
			System.err.println("Year and averages do not match in size...");
			yearly_avgs.close();
			return;
		}

		year_sz = year.size();
		System.out.println("Years: " + year_sz.toString());
		avg_temp_sz = avg_temp.size();
		System.out.println("Average: " + avg_temp_sz.toString());

		double[] a = new double[regression_sz];
		double[] b = new double[regression_sz];
		int end = year.size() - 1;
		int stop = end - regression_sz;
		int j;

		while (true)
		{
			j = regression_sz - 1;
			for (i = end; i > stop; i--)
			{
				a[j] = year.get(i).doubleValue();
				b[j] = avg_temp.get(i).doubleValue();
				if(i == end || i-1 == stop )
					System.out.print(year.get(i).toString() + " ");
				//System.out.println(avg_temp.get(i).toString());
				j--;
			}
			
			lin_reg = new LinearRegression(a, b);
			System.out.println(Double.toString(lin_reg.slope()));

			end = stop;
			if (end == -1)
				break;
			stop -= regression_sz;
			if (stop < 0)
				stop = -1;
		}

//		i = 0;
//		while (i < year.size())
//		{
//			System.out.print(year.get(i).toString() + " ");
//			System.out.println(avg_temp.get(i).toString());
//			i++;
//		}

		yearly_avgs.close();
		return;
	}

}