#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <algorithm>

using namespace std;

double Normal(double zz)
{ 
    //cdf of 0 is 0.5
    if (zz == 0)   
    { 
        return 0.5;
    }
    
    double z = zz;  //zz is input variable,  use z for calculations
    
    if (zz < 0)
        z = -zz;  //change negative values to positive
    
    //set constants
    double p = 0.2316419;  
    double b1 = 0.31938153;
    double b2 = -0.356563782;
    double b3 = 1.781477937;
    double b4 = -1.821255978;
    double b5 = 1.330274428;
    
    //CALCULATIONS
    double f = 1 / sqrt(2 * M_PI);
    double ff = exp(-pow(z, 2) / 2) * f;
    double s1 = b1 / (1 + p * z);
    double s2 = b2 / pow((1 + p * z), 2);
    double s3 = b3 / pow((1 + p * z), 3);
    double s4 = b4 / pow((1 + p * z), 4);
    double s5 = b5 / pow((1 + p * z), 5);
    
    //sz is the right-tail approximation
    double  sz = ff * (s1 + s2 + s3 + s4 + s5); 

    double rz; 
    //cdf of negative input is right-tail of input's absolute value 
    if (zz < 0)
        rz = sz;
    
    //cdf of positive input is one minus right-tail 
    if (zz > 0)
        rz = (1 - sz);
    
    
    return rz;
}

double callValue(double strike, double s, double sd, double r, double days)
{ 
     double ls = log(s);
     double lx = log(strike);
     double t = days / 365;
     double sd2 = pow(sd, 2);
     double n = (ls - lx + r * t + sd2 * t / 2);
     double sqrtT = sqrt(days / 365);
     double d = sd * sqrtT;
     double d1 = n / d;
     double d2 = d1 - sd * sqrtT;
     double nd1 = Normal(d1);
     double nd2 = Normal(d2);
     return s * nd1 - strike * exp(-r * t) * nd2;
}
    
double putValue(double strike, double s, double sd, double r, double days)
{
     double ls = log(s);
     double lx = log(strike);
     double t = days / 365;
     double sd2 = pow(sd, 2);
     double n = (ls - lx + r * t + sd2 * t / 2);
     double sqrtT = sqrt(days / 365);
     double d = sd * sqrtT;
     double d1 = n / d;
     double d2 = d1 - sd * sqrtT;
     double nd1 = Normal(d1);
     double nd2 = Normal(d2);
     return strike * exp(-r * t) * (1 - nd2) - s * (1 - nd1);
}

double blackscholes(
     double asset_price,
     double strike_price,
     double days_to_exp,
     double risk_free_rate,
     double standard_deviation )
{
#if 0
     printf("Strike Price: %f \n", strike_price);
     printf("Asset Price:  %f \n", asset_price);
     printf("Std Dev:      %f \n", standard_deviation);
     printf("Risk Free:    %f \n", risk_free_rate);
     printf("Days to Exp:  %f \n", days_to_exp);
     printf("Put Value:    %f \n", putValue(strike_price, asset_price, standard_deviation, risk_free_rate, days_to_exp));
     printf("Call Value:   %f \n", callValue(strike_price, asset_price, standard_deviation, risk_free_rate, days_to_exp));
 #endif
     double callPrice = callValue(strike_price, asset_price, standard_deviation, risk_free_rate, days_to_exp);
     //printf("%f\n", callPrice);
     return callPrice;
}

int main(int argc, char *argv[])
{
    if(argc<3) { 
	cout << "usage: exe <in_file> <out_file>" << endl;
	exit(-1);
    }
    //vector<double> pricelist;

    ifstream in;
    in.open(argv[1]);

    ofstream of;
    of.open(argv[2]);

    if (in.is_open()) {
     while(in) {
	     double asset_price;
	     double strike_price;
	     double days_to_exp;
	     double risk_free_rate;
	     double standard_deviation;

	     in >> asset_price >> strike_price >> days_to_exp >> risk_free_rate >> standard_deviation;

	     double results = blackscholes(
		       asset_price,
		       strike_price,
		       days_to_exp,
		       risk_free_rate,
		       standard_deviation);
            of << results << endl;
     }
   }

   //sort(pricelist.begin(), pricelist.end());
   // calc the 5% VAR TODO
   //double varResult=0.0;
   //printf("var=%f\n",varResult);
    
   return 0;
}

