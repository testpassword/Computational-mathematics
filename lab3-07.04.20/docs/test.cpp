#include <iostream>
#include <conio.h>
#include <math.h> 
using namespace std;
 
int main()
{
    double x0 = -1, y0 =- 1, x, y, d1, d2, eps = 0.0001;
    int i = 0;
    do {
        i++;
        x = sin(y0) - 2;
        y = 0.5 - cos(x0);
        d1 = sin(y) - x;
        d2 = y + cos(x) - 0.5;
        x0 = x;
        y0 = y;
    } while(abs(d1) > eps && abs(d2) > eps); 
    cout<<"x = "<<x<<" y = "<<y<<" i = "<<i<<endl;
    return 0;
}