#include<bits/stdc++.h>
using namespace std;
#define infinity INT16_MAX
#define pb push_back
#define mp make_pair



class WeightedGraph
{
    int V;
    int** adjmatrix;
    int** pi;
    public:
        WeightedGraph(int V){
            this->V = V;
            adjmatrix = new int*[V+1];
            for(int i=1;i<=V;i++){
                adjmatrix[i] = new int[V+1];
            }
            for(int i=1;i<=V;i++){
                for(int j=1;j<=V;j++){
                    if(i==j)adjmatrix[i][j]=0;
                    else adjmatrix[i][j]=infinity;
                }
            }
            pi = new int*[V+1];
            for(int i=1;i<=V;i++){
                pi[i] = new int[V+1];
            }
             for(int i=1;i<=V;i++){
                for(int j=1;j<=V;j++){
                    pi[i][j]=infinity;
                }
            }
        }


         void addEdge(int u,int v, int w){
            adjmatrix[u][v] = w;
            pi[u][v] = u; 
        }


        vector<vector<int>> extendShortestPaths(vector<vector<int>>L, vector<vector<int>> W){
            vector<vector<int>> temp(V+1, vector<int>(V+1));
            for(int i=1;i<=V;i++){
                for(int j=1;j<=V;j++){
                    temp[i][j]=infinity;
                    for(int k=1;k<=V;k++){
                        if(L[i][k]!=infinity && W[i][k]!=infinity){
                            temp[i][j] = min(temp[i][j], L[i][k] + W[k][j]);
                        }
                    }
                }
            }
            return temp;
        }


        vector<vector<int>> matrixMultiplicationAPSP(){
            vector<vector<int>> L(V+1, vector<int>(V+1));
            for(int i=1;i<=V;i++){
                for(int j=1;j<=V;j++){
                    L[i][j] = adjmatrix[i][j];
                }
            }
            int m=1;
            while(m < V-1){
                L = extendShortestPaths(L,L);
                m*=2;
            }
            return L;
        }



        void printMatrixMultiplication(){
            vector<vector<int>> temp = matrixMultiplicationAPSP();
            bool flag = true;
            for(int i=1;i<=V;i++){
                if(temp[i][i]<0){
                    flag=false;
                    break;
                }
            }

            if(!flag){
                cout<<"Negative Cycle Detected!"<<endl;
                return;
            }
            cout<<"Shortest distance matrix"<<endl;
            for(int i=1;i<=V;i++){
                for(int j=1;j<=V;j++){
                    if(temp[i][j]==infinity){
                        cout<<"INF ";
                    }
                    else{
                        cout<<temp[i][j]<<" ";
                    }
                }
                cout<<endl;
            }
        }




        bool floydWarshall(){
            for(int k=1;k<=V;k++){
                for(int i=1;i<=V;i++){
                    for(int j=1;j<=V;j++){
                        if(adjmatrix[i][k]==infinity||adjmatrix[k][j]==infinity){
                            continue;
                        }
                        else if(adjmatrix[i][j] > adjmatrix[i][k]+adjmatrix[k][j]){
                            adjmatrix[i][j] = adjmatrix[i][k]+adjmatrix[k][j];
                            if(i!=j&&k!=j){
                                pi[i][j] = pi[k][j];
                            }
                        }
                    }
                }
            }

            for(int i=1;i<=V;i++){
                if(adjmatrix[i][i]<0){
                    return false;
                }
            }
            return true;
        }


        void printFloydWarshall(){
            bool flag = floydWarshall();
            if(!flag){
                cout<<"Negative Cycle Detected!"<<endl;
                return;
            }
            cout<<"Shortest distance matrix"<<endl;
            for(int i=1;i<=V;i++){
                for(int j=1;j<=V;j++){
                    if(adjmatrix[i][j]==infinity){
                        cout<<"INF ";
                    }
                    else{
                        cout<<adjmatrix[i][j]<<" ";
                    }
                }
                cout<<endl;
            }

            cout<<endl;
            for(int i=1;i<=V;i++){
                for(int j=1;j<=V;j++){
                    if(pi[i][j]==infinity){
                        cout<<"INF ";
                    }
                    else{
                        cout<<pi[i][j]<<" ";
                    }
                }
                cout<<endl;
            }
        }

};


int main()
{
    int u,v;
    double w;
    int V,E;
    cin>>V>>E;
    WeightedGraph graph(V);
    bool negativeCheck = false;

    for(int i=0;i<E;i++){
        cin>>u>>v>>w;
        graph.addEdge(u, v, w);
    }

    graph.printFloydWarshall();
}
