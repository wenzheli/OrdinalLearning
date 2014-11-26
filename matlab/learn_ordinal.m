
regularizer = 5;
data = trX;
y = trY;
% suppose all the attributes are ordinal 
N = size(data,1);
D = size(data,2);
K = size(unique(y),1);

%% model parameters. 
W = zeros(D*K,1);
psi = zeros(N,1);
intervals = cell(D,1);

E = 0;
cumE = zeros(D,1);    % indicates starting position
%% initialization
for i=1:D
    cumE(i) = E;
    % # of ordinal values for each dimension
    n = size(unique(data(:,i)),1);
    E = E + n-1;   
    interval = linspace(0,1,n+1); % including 0 and 1
    intervals{i}=interval;
end
H = zeros(N*K,1);
G = zeros(N*K,D*K+N);
Q = zeros(D*K+N,1);
% create Q
for j=D*K+1:D*K+N
    Q(j)=regularizer;
end

% initialize p
P = zeros(D*K+N, D*K+N);
for i=1:D*K
    P(i,i)=1;
end

%% EM-step

for itr=1:10
    
    %% Fix M, learn W, quadratic programming. 
    for i=1:N   % for each instance
        instance = data(1,:)';
        for k=1:K % for each label
            if y(i)==k
                H((i-1)*K+k)=0;
            else
                H((i-1)*K+k)=-1;
            end
       
            % fill out G
       
            % convert to real features
            realFeatures = zeros(D,1);
            for j=1:D
                inter = intervals{j};
                realFeatures(j)=(inter(instance(j))+inter(instance(j)+1))/2;
            end
       
            G((i-1)*K+k,D*K+i)=-1;
            if y(i)~=k
                label = y(i);
                % for common version
                j=1;
                for s=(label-1)*D+1:label*D
                    G((i-1)*K+k,s) = realFeatures(j)*(-1);
                    j = j +1;
                end
                % for current version
                j = 1;
                for s=(k-1)*D+1:k*D
                    G((i-1)*K+k,s) = realFeatures(j);
                    j = j +1;
                end 
            end
        end
    end

    x = quadprog(P,Q,G,H);
    W = x(1:D*K,1);
    psi = x(D*K+1:D*K+N);
    
    %% Fix W, learn M, linear programming
    C = zeros(E + N,1);    % objective coefficient
    C(E+1:E+N,1)=1;
    % Ax <= B
    B = zeros(D*K+E+D,1);
    A = zeros(D*K+E+D, E+N);
    
    for i=1:N % for each instance
        instance = data(1,:)';
        for k=1:K   % for each k
            
            % fill out B
            if y(i)==k
                B((i-1)*K+k)=0;
            else
                B((i-1)*K+k)=-1;
            end
            
            % fill out the row of A
            A((i-1)*K+k,E+i)=-1;
            if y(i)~=k
                label = y(i);
                a = W((k-1)*D+1:k*D,1)-W((label-1)*D+1:label*D,1);   % w_k - w_label
                for j=1:D   % iterate through all the attributes
                    interval = intervals{j};
                    if instance(j,1)==1 % only include the first end point
                        A((i-1)*K+k, cumE(j)+1) = 1/2 * a(j);
                    elseif instance(j,1)== size(interval,1)-1 % only include the last end point
                        A((i-1)*K+k, cumE(j)+size(interval,1)-2) = 1/2 * a(j);
                        A((i-1)*K+k,E+i) = A((i-1)*K+k,E+i) - 1/2 * a(j);
                    else  % include both of end points
                        A((i-1)*K+k, cumE(j)+instance(j,1)-1) = 1/2*a(j);
                        A((i-1)*K+k, cumE(j)+instance(j,1)) = 1/2*a(j);
                    end
                end
            
            end
            
            
        end
    end
    
    for i=1:D   % iterate through constraints psi
        interval = intervals{i}';
        for j=1:size(interval,1)-2
            if j==1
                A(N*K+cumE(i)+j,cumE(i)+j) = -1;
            elseif j== size(interval,1)-2
                A(N*K+cumE(i)+j, cumE(i)+j) =1;
                B(N*K+cumE(i)+j,1) = 1;
            else
                A(N*K+cumE(i)+j,cumE(i)+j) = -1;
                A(N*K+cumE(i)+j,cumE(i)+j+1) = 1;
            end
        end
    end
    
    linprog(C,A,B);
       
end



