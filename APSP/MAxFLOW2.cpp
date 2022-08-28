#include<bits/stdc++.h>
using namespace std;


class League{
    public:
        int N;
        vector<string> teamNames;
        vector<int> wins,losses,left;
        vector<vector<int>> adjmat;

        League(){

        }

        League(int N){
            this->N = N;
            vector<vector<int>> temp_adjmat(N+1,vector<int>(N+1));
            this-> adjmat = temp_adjmat;
        }

        void addteamNames(string tName){
            this->teamNames.push_back(tName);
        }

        void addWins(int win){
            this->wins.push_back(win);
        }

        void addLoss(int loss){
            this->losses.push_back(loss);
        }

        void addLeft(int left){
            this->left.push_back(left);
        }

        void addGame(int i, int j, int game){
            this->adjmat[i][j] = game;
        }
};



class MaxFlow{
    public:
        int N;
        int leftTeam;
        int totalGames, totalTeams;
        int totalNodes;
        vector<vector<int>> maximumCapacity;
        vector<vector<int>> edges;
        League league;
        vector<string> nodeNames;


    MaxFlow(League league,int leftTeam){
        this->league = league;
        this->N = league.N;
        this->leftTeam = leftTeam;
        this->totalTeams = N-1;
        this->totalGames = ((totalTeams)*(totalTeams-1))/2;
        this->totalNodes = totalGames + totalTeams + 2;
        vector<vector<int>> temp_maximumCapacity(totalNodes,vector<int>(totalNodes,0));
        this->maximumCapacity = temp_maximumCapacity;
        vector<string> temp_nodeNames(totalNodes,"");
        this->nodeNames = temp_nodeNames;
        
        int k=1;
        nodeNames[0] = "s";
        nodeNames[totalNodes-1] = "t";

        for(int i=0; i<N; i++){
            if( i == leftTeam)continue;
            for(int j = i+1; j<N; j++){
                if(j == leftTeam)continue;

                //Updating the maximum flow from sink(0) to Game Nodes
                maximumCapacity[0][k] = league.adjmat[i][j];
                maximumCapacity[k][0] = 0;
                nodeNames[k] = i + "-" + j;

                //Updating the maximum flow from Game Nodes To Team Nodes
                if(i > leftTeam){
                    maximumCapacity[k][totalGames+i] = 99999;
                    maximumCapacity[totalGames+i][k] = 0;
                    nodeNames[totalGames+i] = league.teamNames[i];
                }
                else{
                    maximumCapacity[k][1+totalGames+i] = 99999;
                    maximumCapacity[1+totalGames+i][k] = 0;
                    nodeNames[1+totalGames+i] = league.teamNames[i];
                }

                if(j > leftTeam){
                    maximumCapacity[k][totalGames+j] = 99999;
                    maximumCapacity[totalGames+j][k] = 0;
                    nodeNames[totalGames+j] = league.teamNames[j];
                }
                else{
                    maximumCapacity[k][1+totalGames+j] = 99999;
                    maximumCapacity[1+totalGames+j][k] = 0;
                    nodeNames[1+totalGames+j] = league.teamNames[j];
                }
                k++;
            }

            //Updating the Maximum Flow From Team Node to Sink Node
            if(i > leftTeam){
                maximumCapacity[totalGames+i][totalNodes-1] = league.wins[leftTeam] + league.left[leftTeam] - league.wins[i];
                if( maximumCapacity[totalGames+i][totalNodes-1]<0) maximumCapacity[totalGames+i][totalNodes-1]=0;
            }
            else{
                maximumCapacity[1+totalGames+i][totalNodes-1] = league.wins[leftTeam] + league.left[leftTeam] - league.wins[i];
                if(maximumCapacity[1+totalGames+i][totalNodes-1]<0)maximumCapacity[1+totalGames+i][totalNodes-1]=0;
            }

        }

    }



    vector<int> bfs(vector<vector<int>> residualNetwork){
        vector<bool> visited(totalNodes, false);
        vector<int> path(totalNodes, -1);
        queue<int> q;
        q.push(0);
        while(!q.empty()){
            int temp = q.front();
            q.pop();
            if(!visited[temp])  visited[temp] = true;
            for(int i=0; i<totalNodes;i++){
                if(!visited[i] && residualNetwork[temp][i]){
                    q.push(i);
                    path[i] = temp;
                    if(visited[totalNodes-1])return path;
                }
            }
        }
        return path;
    }



    vector<int> fordFulkerson(){
        vector<int> certifiedTeams;
        vector<vector<int>> residualNetwork(totalNodes, vector<int>(totalNodes,0));
        int maxFlow = 0;
        int saturation = 0;
        residualNetwork = maximumCapacity;
        for(int i=0; i<totalNodes; i++){
            saturation+=residualNetwork[0][i];
        }
        vector<int> path = bfs(residualNetwork);
        while(path[totalNodes-1]!=-1){
            int minimum = 99999;
            int i = totalNodes-1;
            while(i>0){
                minimum = min(minimum, residualNetwork[path[i]][i]);
                i = path[i];
            }

            i = totalNodes-1;
            while(i>0){
                residualNetwork[path[i]][i] -= minimum;
                residualNetwork[i][path[i]] += minimum;
                i = path[i];
            }
            maxFlow += minimum;
            path = bfs(residualNetwork);
        }


        if(saturation == maxFlow){
            return certifiedTeams;
        }
        else{
            //DFS to find which paths are not saturated and which teams are responsible for elimination
            stack<int> stk;
            vector<bool> visited(totalNodes,false);
            stk.push(0);
            while(!stk.empty()){
                int temp = stk.top();
                stk.pop();
                if(!visited[temp]){
                    visited[temp] = true;
                    for(int i=0; i<totalNodes; i++){
                        if(!visited[i] && residualNetwork[temp][i]){
                            stk.push(i);
                        }
                    }
                }
            }

            for(int i = totalGames+1 ; i<totalNodes-1; i++){
                if(visited[i]){
                    for(int j=0; j<league.teamNames.size(); j++){
                        if(nodeNames[i] == league.teamNames[j]){
                            certifiedTeams.push_back(j);
                            break;
                        }
                    }
                }
            }
            return certifiedTeams;

        }

    }

};



int main()
{
    int N;
    int maxWin = -1;
    int maxWinTeams;
    cin>>N;
    League league(N);
    for(int i=0;i<N;i++){
        string s;
        cin>>s;
        league.addteamNames(s);
        int win,loss,left;
        cin>>win>>loss>>left;
        if(win > maxWin){
            maxWin = win;
        }
        league.addWins(win);
        league.addLoss(loss);
        league.addLeft(left);
        for(int j=0;j<N;j++){
            int matches;
            cin>> matches;
            league.addGame(i,j,matches);
        }
    }
    cout<<endl;

    for(int i=0; i<N; i++){
        if(league.wins[i] == maxWin){
            maxWinTeams = i;break;
        }
    }

    for(int i=0; i<N; i++){
       /* if(league.wins[i] + league.left[i] < maxWin){
            cout<<league.teamNames[i]<<" is eliminated."<<endl;
            cout<<"They can win at most "<<league.wins[i]<<" + "<<league.left[i] <<" = "<<league.wins[i]+league.left[i]<<" games"<<endl;
            cout<<league.teamNames[maxWinTeams]<<" has won a total of "<<league.wins[maxWinTeams]<<" games."<<endl;
            cout<<"They play each other 0 times"<<endl;
            cout<<"So on average, each of the teams in this group wins "<<league.wins[maxWinTeams]<<"/1 = "<<league.wins[maxWinTeams]<<" games"<<endl;
            cout<<endl;
        }
        else{*/
            MaxFlow maxFlow(league, i);
            vector<int> certifiedTeams = maxFlow.fordFulkerson();
            if(certifiedTeams.size()==0)continue;
            else{
                cout<<league.teamNames[i]<<" is eliminated"<<endl;
                cout<<"They can win at most "<<league.wins[i]<<" + "<<league.left[i]<<" = "<< league.wins[i]+league.left[i] <<" games"<<endl;
                int wn = 0, remain = 0;
                cout << league.teamNames[certifiedTeams[0]];
                for (int k = 0; k < certifiedTeams.size(); k++)
                {
                    wn += league.wins[certifiedTeams[k]];
                    if (k != 0 && k != certifiedTeams.size() - 1)
                         cout << ", " << league.teamNames[certifiedTeams[k]];
                    else if (k != 0)
                        cout << " and " << league.teamNames[certifiedTeams[k]];
                }
                sort(certifiedTeams.begin(), certifiedTeams.end());
                for (int m = 0; m < certifiedTeams.size() - 1; m++)
                {
                    for (int n = m + 1; n < certifiedTeams.size(); n++)
                        remain += league.adjmat[certifiedTeams[m]][certifiedTeams[n]];
                }
                cout << " have won a total of " << wn << " games.\nThey play each other " << remain << " times.\nSo on average, each of the team wins " << wn + remain << "/" << certifiedTeams.size() << " = " << float(wn + remain) / float(certifiedTeams.size()) << " games."<<endl;
                cout<<endl;
            }
        //}
    }

}
