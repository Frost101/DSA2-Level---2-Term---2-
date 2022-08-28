#include<bits/stdc++.h>
using namespace std;
#define infinity INT32_MAX
#define pb push_back
#define mp make_pair



class DisjointSets
{
    int *parent;
    int nodes;
    public:
        DisjointSets(int nodes){
            this->nodes = nodes;
            parent = new int[nodes];

            for(int i=0;i<nodes;i++){
                parent[i] = -1;
            }
        }

        int find(int u){
            int vertex = u;
            while(parent[vertex]!=-1){
                vertex = parent[vertex];
            }
            return vertex;
        }

        void merge(int x,int y)
        {
            x = find(x) , y = find(y);
            parent[y] = x;
        }
};




class WeightedGraph
{
    int V;
    list<pair<int,double>> *adjlist;
    int* parent;
    bool* inMST;
    double* weight;
    vector<pair<double,pair<int,int>>> edges; 
    vector<pair<int,int>> kruskalEdges;
    double totalCost=0;

    public:
        WeightedGraph(int V){
            this->V = V;
            adjlist = new list<pair<int,double>>[V];
            parent = new int[V];
            weight = new double[V];
            inMST = new bool[V];
        }

        void initialize(){
            totalCost = 0;
            kruskalEdges.clear();
            for(int i=0;i<V;i++){
                parent[i] = -1;
            }
            for(int i=0;i<V;i++){
                weight[i] = infinity;
            }
            for(int i=0;i<V;i++){
                inMST[i] = false;
            }
        }

        void addEdge(int u,int v, int w){
            adjlist[u].pb(mp(v,w));
            adjlist[v].pb(mp(u,w));
            edges.pb(mp(w,mp(u,v)));
        }

        double prim(){
            initialize();
            priority_queue <pair<double,int>,vector<pair<double,int>>,greater<pair<double,int>>> pq;
            int src = 0;

            pq.push(mp(0,src));
            weight[src] = 0;

            while(!pq.empty()){
                int u = pq.top().second;
                pq.pop();
                
                
                if(inMST[u]){
                    continue;
                }
                else{
                    totalCost+=weight[u];
                    inMST[u] = true;
                }

                for(auto it = adjlist[u].begin(); it!=adjlist[u].end(); it++){
                    int v = it->first;
                    int w = it->second;

                    if(!inMST[v]&&w<weight[v]){
                        weight[v] = w;
                        pq.push(mp(w,v));
                        parent[v] = u;
                    }

                }
            }
            return totalCost;
        }

        void primMSTPrint(){
            prim();
            cout<<"List of edges selected by Prim's: {";
            for(int i=1;i<V;i++){
                cout<<'('<<parent[i]<<','<<i<<')';
                if(i!=V-1)cout<<',';
            }
            cout<<'}'<<endl;
        }

        double kruskal(){
            initialize();
            DisjointSets set(V);
            sort(edges.begin(),edges.end());
            for(auto it = edges.begin();it!=edges.end();it++){
                int u = it->second.first;
                int v = it->second.second;

                int setU = set.find(u);
                int setV = set.find(v);

                if(setU!=setV){
                    kruskalEdges.pb(mp(u,v));
                    totalCost += it->first;
                    set.merge(u,v);
                }
            }
            return totalCost;
        }

        void kruskalMSTPrint(){
            kruskal();
            int i=0;
            cout<<"List Of edges selected by Kruskal's: {";
            for(auto it=kruskalEdges.begin(); it!=kruskalEdges.end(); i++,it++){
                cout<<'('<<it->first<<','<<it->second<<')';
                if(i!=kruskalEdges.size()-1){
                    cout<<',';
                }
            }
            cout<<"}"<<endl;
        }

};


int main()
{

    ifstream input("mst.in.txt");
    int u,v;
    double w;
    int V,E;
    input>>V>>E;
    WeightedGraph graph(V);
    for(int i=0;i<E;i++){
        input>>u>>v>>w;
        graph.addEdge(u, v, w);
    }
    double cost = graph.prim();

    cout<<"Cost of the minimum spanning tree : ";
    cout<<fixed<<setprecision(1)<<cost<<endl;
    graph.primMSTPrint();
    graph.kruskalMSTPrint();
}