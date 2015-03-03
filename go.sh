for ((i=1;i<=10000;i++));
	do   curl --header "Connection: keep-alive" "cloud.nimbits.com/service/backend/gcs";
	printf "\n";
done

