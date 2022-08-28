#include<bits/stdc++.h>
using namespace std;
#define infinity INT32_MAX
#define pb push_back
#define mp make_pair



class WeightedGraph
{
    int V;
    list<pair<int,double>> *adjlist;
    int* parent;
    bool* processed;
    double* distance;
    vector<pair<double,pair<int,int>>> edges;

    public:
        WeightedGraph(int V){
            this->V = V;
            adjlist = new list<pair<int,double>>[V+1];
            parent = new int[V+1];
            distance = new double[V+1];
            processed = new bool[V+1];
        }

        void initialize(){
            for(int i=0;i<V;i++){
                parent[i] = -1;
            }
            for(int i=0;i<V;i++){
                distance[i] = infinity;
            }
            for(int i=0;i<V;i++){
                processed[i] = false;
            }
        }

        void addEdge(int u,int v, int w){
            adjlist[u].pb(mp(v,w));
            edges.pb(mp(w,mp(u,v)));
        }


        void dijkstra(int src){
            initialize();
            priority_queue <pair<double,int>,vector<pair<double,int>>,greater<pair<double,int>>> pq;
            distance[src] = 0;
            pq.push(mp(0,src));
            while(!pq.empty()){
                int u = pq.top().second;
                int w = pq.top().first;
                pq.pop();
                if(processed[u])continue;
                processed[u]=true;

                for(auto it = adjlist[u].begin(); it!=adjlist[u].end(); it++){
                    int v = it->first;
                    int dist = it->second;
                    if(!processed[v] && w+dist < distance[v] && w!=infinity){
                        parent[v] = u;
                        distance[v] = w + dist;
                        pq.push(mp(distance[v],v));
                    }
                }
            }
        }


        double dijkstraCost(int src, int dest){
            dijkstra(src);
            return distance[dest];
        }

        void dijkstraPath(int src, int dest){
            dijkstra(src);
            vector<int> path;
            int prnt;
            while(dest!=src){
                path.push_back(dest);
                dest = parent[dest];
            }
            path.pb(src);
            for(int i = path.size()-1 ; i>=0; i--){
                cout<<path[i];
                if(i!=0)cout<<" -> ";
                else cout<<endl;
            }
        }


        bool bellmanFord(int src){
            initialize();
            distance[src] = 0;
            for(int i=1;i<=V-1;i++){
                for(auto it = edges.begin(); it!=edges.end(); it++){
                    int u = it->second.first;
                    int v = it->second.second;
                    double w = it->first;
                    if(distance[u]!=infinity && distance[u]+w < distance[v]){
                        distance[v] = distance[u] + w;
                        parent[v] = u;
                    }
                }
            }


            bool flag = true;
            for(auto it = edges.begin(); it!=edges.end(); it++){
                int u = it->second.first;
                int v = it->second.second;
                double w = it->first;
                if(distance[u]!=infinity && distance[u]+w < distance[v]){
                    flag = false;
                    break;
                }
            }

            return flag;
        }

        double bellmanFordCost(int src, int dest){
            bellmanFord(src);
            return distance[dest];
        }

        void bellmanFordPath(int src, int dest){
            bellmanFord(src);
            vector<int> path;
            int prnt;
            while(dest!=src){
                path.push_back(dest);
                dest = parent[dest];
            }
            path.pb(src);
            for(int i = path.size()-1 ; i>=0; i--){
                cout<<path[i];
                if(i!=0)cout<<" -> ";
                else cout<<endl;
            }

        }
};


int main()
{

    ifstream input("input.txt");
    int u,v;
    double w;
    int V,E;
    input>>V>>E;
    WeightedGraph graph(V);
    bool negativeCheck = false;
    for(int i=0;i<E;i++){
        input>>u>>v>>w;
        graph.addEdge(u, v, w);
        if(w<0)negativeCheck = true;
    }
    int src,dest;
    input>>src>>dest;

    if(!negativeCheck){
        cout<<"Shortest path cost: "<<graph.dijkstraCost(src,dest)<<endl;
        graph.dijkstraPath(src,dest);
    }
    else{
        if(!graph.bellmanFord(src)){
            cout<<"The graph contains a negative cycle"<<endl;
        }
        else{
            cout<<"The graph does not contain a negative cycle"<<endl;
            cout<<"Shortest path cost: "<<graph.bellmanFordCost(src, dest)<<endl;
            graph.bellmanFordPath(src, dest);
        }
    }

}